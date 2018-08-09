package com.team2502.ezauton.command;

import com.team2502.ezauton.utils.ICopyableStopwatch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Describes a group of multiple IActions which itself is also an IAction
 */
public class ActionGroup implements IAction
{
    private List<ActionWrapper> scheduledActions;
    private List<Runnable> onFinish = new ArrayList<>();

    private IAction lastSimulated;

    private ICopyableStopwatch stopwatch;

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
     * @param millisPeriod How often the the thread will loop
     * @return The thread, ready to start.
     */
    @Override
    public Thread buildThread(long millisPeriod)
    {
        return new Thread(() -> {
            List<ActionWrapper> scheduledActions = new ArrayList<>(this.scheduledActions);
            List<Thread> withCommands = new ArrayList<>();
            while(!scheduledActions.isEmpty() && !Thread.currentThread().isInterrupted())
            {
                ActionWrapper currentAction = scheduledActions.get(0);

                boolean broke = false;
                if(currentAction.type == Type.WITH || currentAction.type == Type.PARALLEL)
                {
                    Iterator<ActionWrapper> iterator = scheduledActions.iterator();
                    whileloop:
                    while(iterator.hasNext())
                    {
                        ActionWrapper next = iterator.next();
                        switch(next.type)
                        {
                            case WITH:
                            case PARALLEL:
                                Thread thread = next.getAction().buildThread(millisPeriod);
                                iterator.remove();
                                if(next.type == Type.WITH)
                                {
                                    withCommands.add(thread);
                                }
                                thread.start();
                                break;
                            case SEQUENTIAL:
                                currentAction = next;
                                broke = true;
                                break whileloop;
                        }
                    }
                    if(!broke)
                    {
                        break;
                    }
                }

                Thread thread = currentAction.getAction().buildThread(millisPeriod);
                thread.start();
                try
                {
                    thread.join();
                }
                catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
                withCommands.forEach(Thread::interrupt);
                withCommands.clear();
                scheduledActions.remove(0);

                try
                {
                    Thread.sleep(millisPeriod);
                }
                catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            onFinish.forEach(Runnable::run);
        });

    }

    /**
     * Simulate the execution of this action group
     *
     * @param millisPeriod How often to loop
     */
    @Override
    public void simulate(long millisPeriod)
    {
        List<ActionWrapper> scheduledActions = new ArrayList<>(this.scheduledActions);
        if(scheduledActions.size() == 0)
        {
            return;
        }
        ActionWrapper currentAction = scheduledActions.get(0);

        List<IAction> withActions = new ArrayList<>();

        Type type = currentAction.getType();
        ifblock:
        if(type == Type.PARALLEL || type == Type.WITH)
        {
            Iterator<ActionWrapper> iterator = scheduledActions.iterator();
            whileloop:
            while(iterator.hasNext())
            {
                ActionWrapper next = iterator.next();
                switch(next.type)
                {
                    case PARALLEL:
                    case WITH:
                        next.getAction().simulate(millisPeriod);
                        if(next.type == Type.WITH)
                        {
                            withActions.add(next.action);
                        }
                        iterator.remove();
                        break;
                    case SEQUENTIAL:
                        currentAction = next;
                        break ifblock;
                }
            }
            return;
        }

        IAction currentSequential = currentAction.getAction();
        scheduledActions.remove(0);

        currentSequential.onFinish(() -> withActions.forEach(IAction::removeSimulator));
        Runnable scheduleNextActions = () -> new ActionGroup(scheduledActions).simulate(millisPeriod);

        currentSequential.onFinish(scheduleNextActions);

        if(scheduledActions.stream().allMatch(actionWrapper ->
                                                       actionWrapper.type == Type.WITH ||
                                                       actionWrapper.type == Type.PARALLEL))
        {
            currentSequential.onFinish(() -> onFinish.forEach(Runnable::run));
        }

        currentSequential.simulate(millisPeriod);
    }

    @Override
    public void removeSimulator()
    {
        if(lastSimulated != null)
        {
            lastSimulated.removeSimulator();
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

    public List<ActionWrapper> getScheduledActions()
    {
        return scheduledActions;
    }
}
