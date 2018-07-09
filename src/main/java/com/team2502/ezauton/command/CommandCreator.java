package com.team2502.ezauton.command;

import com.team2502.ezauton.utils.RealStopwatch;
import edu.wpi.first.wpilibj.command.Command;

/**
 * Creates a command from an {@link IAction}. This allows for abstraction and use of commands in simulators.
 */
public class CommandCreator extends Command
{

    private final IAction iCommand;
    private final RealStopwatch stopwatch;

    public CommandCreator(IAction iCommand)
    {
        this.iCommand = iCommand;
        stopwatch = new RealStopwatch();
    }

    @Override
    protected void initialize()
    {
        iCommand.init(stopwatch);
    }

    @Override
    protected void execute()
    {
        iCommand.execute();
    }

    @Override
    protected boolean isFinished()
    {
        return iCommand.isFinished();
    }
}
