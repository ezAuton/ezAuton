package com.team2502.ezauton.command;

import com.team2502.ezauton.utils.IClock;
import com.team2502.ezauton.utils.ICopyable;

import java.util.ArrayList;
import java.util.List;

/**
 * Describes a group of multiple IActions which itself is also an IAction
 */
public class ActionGroup extends AbstractAction
{
    private List<ActionWrapper> scheduledActions;
    private List<Runnable> onFinish = new ArrayList<>();

    private IAction lastSimulated;

    private ICopyable stopwatch;

    /**
     * Creates an ActionGroup comprised of sequential commands
     *
     * @param actions The actions to run in sequence
     */
    public ActionGroup(IAction... actions)
    {
        this.scheduledActions = new ArrayList<>();
        for(IAction action : actions)
        {
            this.scheduledActions.add(new ActionWrapper(action, Type.SEQUENTIAL));
        }
    }

    /**
     * Creates an Action Group comprised of different kinds of commands (i.e sequential, parallel, with)
     *
     * @param scheduledActions The ActionWrappers to run
     */
    public ActionGroup(List<ActionWrapper> scheduledActions)
    {
        this.scheduledActions = scheduledActions;
    }

    /**
     * Add a sequential Action to the actions that we will run
     *
     * @param action The Action to run
     * @return this
     */
    public ActionGroup addSequential(IAction action)
    {
        this.scheduledActions.add(new ActionWrapper(action, Type.SEQUENTIAL));
        return this;
    }

    /**
     * Add a daemonic Action to the actions that we will run. It will run in parallel with another action, except it wil end at the same time as the other action.
     *
     * @param action The Action to run
     * @return this
     */
    public ActionGroup with(IAction action)
    {
        this.scheduledActions.add(new ActionWrapper(action, Type.WITH));
        return this;
    }

    /**
     * Add a parallel Action to the actions that we will run. It will run in parallel and will end in its own time.
     *
     * @param action The action to run
     * @return this
     */
    public ActionGroup addParallel(IAction action)
    {
        this.scheduledActions.add(new ActionWrapper(action, Type.PARALLEL));
        return this;
    }

    /**
     * Create a Thread from this Action group
     *
     * @param clock The clock that will be given to our actions
     * @return The thread, ready to start.
     */
    @Override
    public void run(IClock clock)
    {
        List<IAction> withActions = new ArrayList<>();
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
                    new ThreadBuilder(action, clock).buildAndRun();
                    break;
                case SEQUENTIAL:
                    action.run(clock);
                    withActions.forEach(IAction::stop);
                    withActions.clear();
            }
        }
    }


    /**
     * Add something to run when finished
     *
     * @param onFinish The thing to run
     * @return this
     */
    @Override
    public ActionGroup onFinish(Runnable onFinish)
    {
        this.onFinish.add(onFinish);
        return this;
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
    protected static class ActionWrapper
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