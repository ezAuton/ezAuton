package com.github.ezauton.core.action;

import com.github.ezauton.core.action.tangible.ActionCallable;

import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


/**
 * Describes a group of multiple IActions which itself is also an Action
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
    public static ActionGroup ofSequentials(Action... actions) {
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
    public static ActionGroup ofParallels(Action... actions) {
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
    public final ActionGroup addSequential(Action action) {
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
    public final ActionGroup with(Action action) {
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
    public final ActionGroup addParallel(Action action) {
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

        private final Action action;
        private final Future<Void> future;

        WithActionData(Action action, Future<Void> future) {
            this.action = action;
            this.future = future;
        }

        public Action getAction() {
            return action;
        }

        public Future<Void> getFuture() {
            return future;
        }
    }

    @Override
    public final void run(ActionRunInfo actionRunInfo) throws ExecutionException {
        List<WithActionData> withActions = new ArrayList<>();
        List<Future<Void>> actionFutures = new ArrayList<>();

        for (ActionWrapper scheduledAction : scheduledActions) {
            if(Thread.interrupted()){
                cancelAll(actionFutures);
                return;
            }
            Action action = scheduledAction.getAction();

            final Future<Void> submit = actionRunInfo.getActionScheduler().scheduleAction(action);
            actionFutures.add(submit);

            switch (scheduledAction.getType()) {
                case WITH:
                    withActions.add(new WithActionData(action, submit));
                    break;
                case PARALLEL:
                    break;
                case SEQUENTIAL:
                    try {
                        /*
                        It might seem odd why we are scheduling even sequential actions.
                        You might ask — "why can't we just run the callable directly?"

                        The reason is we would never get the interruption exception...SA WOULD
                        we are not the ones blocking ... THE SA is.
                        Thus, to be able to process InterruptedExceptions, the action must be running detached from
                        the action group.
                         */
                        submit.get();
                    }
                    catch (InterruptedException e){
                        cancelAll(actionFutures);
                        return;
                    }
                    catch (CancellationException e) {
                        cancelAll(actionFutures);
                        throw new ExecutionException("A sequential action threw an exception", e);
                    }

                    for (WithActionData withAction : withActions) {
                        final Future<Void> future = withAction.getFuture();
                        if (!future.isDone()) {
                            try {
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
            System.out.println("reeeee");
            // If we get interrupted
            cancelAll(actionFutures);
        }
    }

    private void cancelAll(Collection<Future<Void>> futures) {
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
     * Describes a wrapper class for Action that also contains the concurrency level
     *
     * @see Type
     */
    public static final class ActionWrapper {

        private final Type type;
        private final Action action;

        public ActionWrapper(Action action, Type type) {
            this.type = type;
            this.action = action;
        }

        public Type getType() {
            return type;
        }

        public Action getAction() {
            return action;
        }
    }
}
