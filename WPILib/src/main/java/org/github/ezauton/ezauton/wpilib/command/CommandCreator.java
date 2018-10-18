package org.github.ezauton.ezauton.wpilib.command;

import org.github.ezauton.ezauton.action.IAction;
import org.github.ezauton.ezauton.action.PeriodicAction;
import org.github.ezauton.ezauton.action.ThreadBuilder;
import edu.wpi.first.wpilibj.command.Command;

/**
 * Creates a action from an {@link PeriodicAction}. This allows for abstraction and use of commands in simulators.
 */
public class CommandCreator extends Command //TODO: Change name?
{

    private final IAction action;
    private Thread thread;
    private boolean finished = false;

    /**
     * Create a action from a PeriodicAction
     *
     * @param action The action to run as a action
     */
    public CommandCreator(IAction action)
    {
        this.action = action;
    }

    @Override
    protected void initialize()
    {
        thread = new ThreadBuilder(action).build();
        action.onFinish(()->finished = true);
    }

    @Override
    protected void execute()
    {
        //TODO: Fix publicity of PeriodicAction#execute or location of CommandCreator such that the following line of code compiles
//        action.execute();
    }

    @Override
    protected boolean isFinished()
    {
//        boolean finished = action.isFinished();
        if(finished)
        {
            action.getFinished().forEach(Runnable::run);
        }
        return finished;
    }

    @Override
    protected void end()
    {
        action.end();
        thread.stop();
    }

    @Override
    protected void interrupted()
    {
        action.end();
        thread.interrupt();
    }
}
