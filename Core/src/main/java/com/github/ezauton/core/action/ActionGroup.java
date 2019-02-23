package com.github.ezauton.core.action;

import com.github.ezauton.core.action.tangible.ActionCallable;

import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


/**
 * Describes a group of multiple IActions which itself is also an IAction
 * <p>
 * Create a Thread from this Action group. The ActionGroup blocks until all sub actions (including parallels) are
 * finished. The reason for this is mentioned
 * <a href = "https://vorpus.org/blog/notes-on-structured-concurrency-or-go-statement-considered-harmful/">here</a>.
 */
public final class ActionGroup extends BaseAction {
    private Queue<ActionWrapper> scheduledActions;

    /**
     * Creates an Action Group comprised of different kinds of commands (i.e sequential, parallel, with)
     *
     * @param scheduledActions The ActionWrappers to run
     */
    public ActionGroup(ActionWrapper... scheduledActions) {
        this.scheduledActions = new LinkedList<>(Arrays.asList(scheduledActions));
    }

    /**
     * Create an empty ActionGroup
     */
    public ActionGroup() {
        this.scheduledActions = new LinkedList<>();
    }

    /**
     * Create an action group of sequential actions
     *
     * @param actions
     * @return
     */
    public static ActionGroup ofSequentials(IAction... actions) {
        ActionGroup actionGroup = new ActionGroup();
        Arrays.stream(actions).forEach(actionGroup::addSequential);
        return actionGroup;
    }


    /**
     * A utility method to merge multiple {@link Runnable}s in to one {@link Runnable}
     *
     * @param runnables the merged {@link Runnable}
     * @return
     */
    public static Runnable mergeRunnablesSequential(Runnable... runnables) {
        return () -> Arrays.stream(runnables).forEach(Runnable::run);
    }

    /**
     * Creates an {@link ActionGroup} with a sequential action for each {@link Runnable}
     *
     * @param runnables
     * @return
     */
    public static ActionGroup ofSequentials(Runnable... runnables) {
        ActionGroup actionGroup = new ActionGroup();
        Arrays.stream(runnables).forEach(actionGroup::addSequential);
        return actionGroup;
    }

    /**
     * Creates an {@link ActionGroup} with parallel actions for each {@link Runnable}
     *
     * @param actions
     * @return
     */
    public static ActionGroup ofParallels(IAction... actions) {
        ActionGroup actionGroup = new ActionGroup();
        Arrays.stream(actions).forEach(actionGroup::addParallel);
        return actionGroup;
    }

    /**
     * Creates an {@link ActionGroup} with parallel actions for each {@link Runnable}
     *
     * @param runnables actions to run
     * @return
     */
    public static ActionGroup ofParallels(Runnable... runnables) {
        ActionGroup actionGroup = new ActionGroup();
        Arrays.stream(runnables).forEach(actionGroup::addParallel);
        return actionGroup;
    }

    /**
     * Add a sequential Action to the actions that we will run
     *
     * @param action The Action to run
     * @return this
     */
    public final ActionGroup addSequential(IAction action) {
        this.scheduledActions.add(new ActionWrapper(action, Type.SEQUENTIAL));

        return this;
    }

    /**
     * Add a sequential Action to the actions that we will run
     *
     * @param runnable The Action to run
     * @return this
     */
    public final ActionGroup addSequential(Runnable runnable) {
        this.scheduledActions.add(new ActionWrapper(new BaseAction(runnable), Type.SEQUENTIAL));
        return this;
    }

    /**
     * Add a daemonic Action to the actions that we will run. It will run in parallel with another action, except it wil end at the same time as the other action.
     *
     * @param action The Action to run
     * @return this
     */
    public final ActionGroup with(IAction action) {
        this.scheduledActions.add(new ActionWrapper(action, Type.WITH));
        return this;
    }

    /**
     * Add a daemonic Action to the actions that we will run. It will run in parallel with another action, except it wil end at the same time as the other action.
     *
     * @param runnable The Runnable to run
     * @return this
     */
    public final ActionGroup with(Runnable runnable) {
        this.scheduledActions.add(new ActionWrapper(new BaseAction(runnable), Type.WITH));
        return this;
    }

    /**
     * Add a parallel Action to the actions that we will run. It will run in parallel and will end in its own time.
     *
     * @param action The action to run
     * @return this
     */
    public final ActionGroup addParallel(IAction action) {
        this.scheduledActions.add(new ActionWrapper(action, Type.PARALLEL));
        return this;
    }

    /**
     * Add a parallel Action to the actions that we will run. It will run in parallel and will end in its own time.
     *
     * @param runnable The Runnable to run
     * @return this
     */
    public final ActionGroup addParallel(Runnable runnable) {
        this.scheduledActions.add(new ActionWrapper(new BaseAction(runnable), Type.PARALLEL));
        return this;
    }

    class WithActionData {

        private final IAction action;
        private final Future<Void> future;

        WithActionData(IAction action, Future<Void> future) {
            this.action = action;
            this.future = future;
        }

        public IAction getAction() {
            return action;
        }

        public Future<Void> getFuture() {
            return future;
        }
    }

    @Override
    public final void run(ActionRunInfo actionRunInfo) throws ExecutionException {
        List<WithActionData> withActions = new ArrayList<>();
//        List<Future<Void>> withActionFutures = new ArrayList<>();
        List<Future<Void>> actionFutures = new ArrayList<>();

        for (ActionWrapper scheduledAction : scheduledActions) {
            IAction action = scheduledAction.getAction();

            switch (scheduledAction.getType()) {
                case WITH:
                case PARALLEL:

                    final Future<Void> submit = actionRunInfo.getActionScheduler().scheduleAction(action);

                    actionFutures.add(submit);

                    if (scheduledAction.getType() == Type.WITH) withActions.add(new WithActionData(action, submit));
                    break;
                case SEQUENTIAL:
                    try {
                        new ActionCallable(action, actionRunInfo).call();
                    } catch (Exception e) {
                        finishUp(actionFutures);
                        throw new ExecutionException("A sequential action threw an exception", e);
                    }

                    for (WithActionData withAction : withActions) {
                        final Future<Void> future = withAction.getFuture();
                        if (!future.isDone()) {
                            try {
                                withAction.getAction().interrupted();
                                future.cancel(true);
                            } catch (Exception e) {
                                throw new ExecutionException("Exception in interrupt()", e);
                            }
                        }
                    }
                    withActions.clear();
            }
        }
        try {
            for (Future<Void> actionFuture : actionFutures) {
                try {
                    actionFuture.get();
                } catch (CancellationException ignored) {
                    // We are excepting exceptions to be cancelled if this
                }
            }
        } catch (InterruptedException e) {
            // If we get interrupted
            finishUp(actionFutures);
        }
    }


    private void finishUp(Collection<Future<Void>> futures) {
        futures.forEach(voidFuture -> voidFuture.cancel(true));
    }

    /**
     * Provides a way to describe the concurrency of an action
     */
    public enum Type {
        /**
         * Runs in parallel to everything else.
         */
        PARALLEL,

        /**
         * Runs sequentially. Only one sequential action can be running at a time
         */
        SEQUENTIAL,

        /**
         * Runs in parallel to everything else, but will end when the current sequential action ends.
         */
        WITH
    }

    /**
     * Describes a wrapper class for IAction that also contains the concurrency level
     *
     * @see Type
     */
    public static final class ActionWrapper {

        private final Type type;
        private final IAction action;

        public ActionWrapper(IAction action, Type type) {
            this.type = type;
            this.action = action;
        }

        public Type getType() {
            return type;
        }

        public IAction getAction() {
            return action;
        }
    }
}
