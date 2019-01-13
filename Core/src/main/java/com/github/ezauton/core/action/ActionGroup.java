package com.github.ezauton.core.action;

import com.github.ezauton.core.utils.IClock;

import java.util.*;


/**
 * Describes a group of multiple IActions which itself is also an IAction
 */
public final class ActionGroup extends BaseAction
{
    private Queue<ActionWrapper> scheduledActions;

    /**
     * Creates an Action Group comprised of different kinds of commands (i.e sequential, parallel, with)
     *
     * @param scheduledActions The ActionWrappers to run
     */
    public ActionGroup(ActionWrapper... scheduledActions)
    {
        this.scheduledActions = new LinkedList<>(Arrays.asList(scheduledActions));
    }

    public ActionGroup()
    {
        this.scheduledActions = new LinkedList<>();
    }

    public static ActionGroup ofSequentials(IAction... actions)
    {
        ActionGroup actionGroup = new ActionGroup();
        Arrays.stream(actions).forEach(actionGroup::addSequential);
        return actionGroup;
    }


    public static Runnable mergeRunnablesSequential(Runnable... runnables)
    {
        return () -> Arrays.stream(runnables).forEach(Runnable::run);
    }

    public static ActionGroup ofSequentials(Runnable... runnables)
    {
        ActionGroup actionGroup = new ActionGroup();
        Arrays.stream(runnables).forEach(actionGroup::addSequential);
        return actionGroup;
    }

    public static ActionGroup ofParallels(IAction... actions)
    {
        ActionGroup actionGroup = new ActionGroup();
        Arrays.stream(actions).forEach(actionGroup::addParallel);
        return actionGroup;
    }

    public static ActionGroup ofParallels(Runnable... runnables)
    {
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
    public final ActionGroup addSequential(IAction action)
    {
        this.scheduledActions.add(new ActionWrapper(action, Type.SEQUENTIAL));

        return this;
    }

    /**
     * Add a sequential Action to the actions that we will run
     *
     * @param runnable The Action to run
     * @return this
     */
    public final ActionGroup addSequential(Runnable runnable)
    {
        this.scheduledActions.add(new ActionWrapper(new BaseAction(runnable), Type.SEQUENTIAL));
        return this;
    }

    /**
     * Add a daemonic Action to the actions that we will run. It will run in parallel with another action, except it wil end at the same time as the other action.
     *
     * @param action The Action to run
     * @return this
     */
    public final ActionGroup with(IAction action)
    {
        this.scheduledActions.add(new ActionWrapper(action, Type.WITH));
        return this;
    }

    /**
     * Add a daemonic Action to the actions that we will run. It will run in parallel with another action, except it wil end at the same time as the other action.
     *
     * @param runnable The Runnable to run
     * @return this
     */
    public final ActionGroup with(Runnable runnable)
    {
        this.scheduledActions.add(new ActionWrapper(new BaseAction(runnable), Type.WITH));
        return this;
    }

    /**
     * Add a parallel Action to the actions that we will run. It will run in parallel and will end in its own time.
     *
     * @param action The action to run
     * @return this
     */
    public final ActionGroup addParallel(IAction action)
    {
        this.scheduledActions.add(new ActionWrapper(action, Type.PARALLEL));
        return this;
    }

    /**
     * Add a parallel Action to the actions that we will run. It will run in parallel and will end in its own time.
     *
     * @param runnable The Runnable to run
     * @return this
     */
    public final ActionGroup addParallel(Runnable runnable)
    {
        this.scheduledActions.add(new ActionWrapper(new BaseAction(runnable), Type.PARALLEL));
        return this;
    }

    /**
     * Create a Thread from this Action group
     *
     * @param clock The clock that will be given to our actions
     * @return The thread, ready to start.
     */
    @Override
    public final void run(IClock clock)
    {
        List<IAction> withActions = new ArrayList<>();
        List<Thread> withActionThreads = new ArrayList<>();

        Set<Thread> threadsToJoin = new HashSet<>();

        for(ActionWrapper scheduledAction : scheduledActions)
        {
            if(isStopped())
            {
                return;
            }
            IAction action = scheduledAction.getAction();

            switch(scheduledAction.getType())
            {
                case WITH:
                    withActions.add(action);

                case PARALLEL:
                    Thread start = new ThreadBuilder(action, clock).start();
                    threadsToJoin.add(start);
                    if(scheduledAction.getType() == Type.WITH) withActionThreads.add(start);
                    break;
                case SEQUENTIAL:
                    action.run(clock);

                    action.getFinished().forEach(Runnable::run);

                    withActionThreads.forEach(Thread::interrupt);
                    withActions.forEach(IAction::end);

                    withActions.clear();
            }
        }
        try {
            for (Thread thread : threadsToJoin) {
                thread.join();
            }
        }catch (InterruptedException e) {
            threadsToJoin.forEach(Thread::interrupt);
            e.printStackTrace();
        }
    }

    /**
     * Provides a way to describe the concurrency of an action
     */
    public enum Type
    {
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
    public static final class ActionWrapper
    {

        private final Type type;
        private final IAction action;

        public ActionWrapper(IAction action, Type type)
        {
            this.type = type;
            this.action = action;
        }

        public Type getType()
        {
            return type;
        }

        public IAction getAction()
        {
            return action;
        }
    }
}
