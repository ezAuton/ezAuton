package com.team2502.ezauton.wpilib.command;

import com.team2502.ezauton.command.SimpleAction;
import com.team2502.ezauton.utils.RealStopwatch;
import edu.wpi.first.wpilibj.command.Command;

/**
 * Creates a command from an {@link SimpleAction}. This allows for abstraction and use of commands in simulators.
 */
public class CommandCreator extends Command //TODO: Change name?
{

    private final SimpleAction action;

    /**
     * Create a command from a BaseAction
     *
     * @param action The action to run as a command
     */
    public CommandCreator(SimpleAction action)
    {
        this.action = action;
    }

    @Override
    protected void initialize()
    {
        action.init(new RealStopwatch());
    }

    @Override
    protected void execute()
    {
        action.execute();
    }

    @Override
    protected boolean isFinished()
    {
        boolean finished = action.isFinished();
        if(finished)
        {
            action.getFinished().forEach(Runnable::run);
        }
        return finished;
    }

    @Override
    protected void end()
    {

    }

    @Override
    protected void interrupted()
    {
        end();
    }
}
