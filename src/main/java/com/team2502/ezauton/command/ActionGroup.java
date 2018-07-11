package com.team2502.ezauton.command;

import com.team2502.ezauton.utils.ICopyableStopwatch;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ActionGroup implements IAction
{
    private List<ActionWrapper> scheduledActions;
    private List<Runnable> onFinish = new ArrayList<>();

    private IAction lastSimulated;

    private ICopyableStopwatch stopwatch;

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
    public Command buildWPI()
    {
        CommandGroup commandGroup = new CommandGroup();
        List<Command> withCommands = new ArrayList<>();

        for(ActionWrapper scheduledAction : scheduledActions)
        {
            IAction action = scheduledAction.getAction();
            Command command = action.buildWPI();
            Type type = scheduledAction.getType();

            switch(type)
            {
                case WITH:
                    withCommands.add(command);
                case PARALLEL:
                    commandGroup.addParallel(command);
                    break;
                case SEQUENTIAL:
                    commandGroup.addSequential(command);
                    if(!withCommands.isEmpty())
                    {
                        InstantAction instantAction = new InstantAction(() -> withCommands.forEach(Command::cancel));
                        commandGroup.addSequential(instantAction.buildWPI());
                        withCommands.clear();
                    }
                    break;
            }
        }
        InstantAction instantAction = new InstantAction(() -> onFinish.forEach(Runnable::run));
        commandGroup.addSequential(instantAction.buildWPI());
        return commandGroup;
    }

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
                                withCommands.clear();
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

    @Override
    public void simulate(long millisPeriod)
    {
        List<ActionWrapper> scheduledActions = new ArrayList<>(this.scheduledActions);
        ActionWrapper currentAction = scheduledActions.get(0);

        boolean broke = false;
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
                        iterator.remove();
                        break;
                    case SEQUENTIAL:
                        currentAction = next;
                        break ifblock;
                }
            }
            return;
        }

        IAction action = currentAction.getAction();
        scheduledActions.remove(0);
        if(!scheduledActions.isEmpty())
        {
            action.onFinish(() -> new ActionGroup(scheduledActions).simulate(millisPeriod));
        }
        else
        {
            action.onFinish(() -> onFinish.forEach(Runnable::run));
        }
        action.simulate(millisPeriod);
    }

    @Override
    public void removeSimulator()
    {
        if(lastSimulated != null)
        {
            lastSimulated.removeSimulator();
        }
    }

    @Override
    public void onFinish(Runnable onFinish)
    {
        this.onFinish.add(onFinish);
    }

    public enum Type
    {
        PARALLEL,
        SEQUENTIAL,
        WITH
    }

    private static class ActionWrapper
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
