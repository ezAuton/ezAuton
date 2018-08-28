package com.team2502.ezauton.wpilib.command;

import com.team2502.ezauton.command.IAction;
import com.team2502.ezauton.command.SimpleAction;
import com.team2502.ezauton.command.ThreadBuilder;
import edu.wpi.first.wpilibj.command.Command;

/**
 * Creates a command from an {@link SimpleAction}. This allows for abstraction and use of commands in simulators.
 */
public class CommandCreator extends Command
{

    private final IAction action;
    private Thread thread;
    private boolean finished = false;


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
    {}

    @Override
    protected boolean isFinished()
    {
        return finished;
    }

    @Override
    protected void end()
    {
        action.end();
        thread.interrupt();
    }

    @Override
    protected void interrupted()
    {
        thread.stop();
    }
}
