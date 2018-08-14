package com.team2502.ezauton.command;

import com.team2502.ezauton.utils.IClock;
import com.team2502.ezauton.utils.ICopyable;

import java.util.ArrayList;
import java.util.List;

public class ActionGroup extends AbstractAction
{
    private List<ActionWrapper> scheduledActions;
    private List<Runnable> onFinish = new ArrayList<>();

    private IAction lastSimulated;

    private ICopyable stopwatch;

    /**
     * Creates an ActionGroup comprised of sequential commands
     *
     * @param actions
     */
    public ActionGroup(IAction... actions)
    {
        this.scheduledActions = new ArrayList<>();
        for(IAction action : actions)
        {
            this.scheduledActions.add(new ActionWrapper(action, Type.SEQUENTIAL));
        }
    }

    public ActionGroup(List<ActionWrapper> scheduledActions)
    {
        this.scheduledActions = scheduledActions;
    }

    public ActionGroup addSequential(IAction action)
    {
        this.scheduledActions.add(new ActionWrapper(action, Type.SEQUENTIAL));
        return this;
    }

    public ActionGroup with(IAction action)
    {
        this.scheduledActions.add(new ActionWrapper(action, Type.WITH));
        return this;
    }

    public ActionGroup addParallel(IAction action)
    {
        this.scheduledActions.add(new ActionWrapper(action, Type.PARALLEL));
        return this;
    }

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

    @Override
    public ActionGroup onFinish(Runnable onFinish)
    {
        this.onFinish.add(onFinish);
        return this;
    }

    public List<ActionWrapper> getScheduledActions()
    {
        return scheduledActions;
    }

    public enum Type
    {
        PARALLEL,
        SEQUENTIAL,
        WITH
    }

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
