package com.team2502.ezauton.command;

import edu.wpi.first.wpilibj.command.Command;

/**
 * Creates a command from an {@link IAction}. This allows for abstraction and use of commands in simulators.
 */
public class CommandCreator extends Command
{

    private final IAction iCommand;

    public CommandCreator(IAction iCommand)
    {
        this.iCommand = iCommand;
    }

    @Override
    protected void initialize()
    {
        iCommand.init();
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
