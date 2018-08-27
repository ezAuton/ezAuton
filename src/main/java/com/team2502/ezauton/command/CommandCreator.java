package com.team2502.ezauton.command;

import com.team2502.ezauton.utils.RealStopwatch;
import edu.wpi.first.wpilibj.command.Command;

/**
 * Creates a command from an {@link BaseAction}. This allows for abstraction and use of commands in simulators.
 */
public class CommandCreator extends Command //TODO: Change name?
{

    private final BaseAction action;

    /**
     * Create a command from a BaseAction
     *
     * @param action The action to run as a command
     */
    public CommandCreator(BaseAction action)
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
            action.getRunnables().forEach(Runnable::run);
        }
        return finished;
    }
}
