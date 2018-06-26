package com.team2502.ezauton.command;

import edu.wpi.first.wpilibj.command.Command;

public class CommandCreator extends Command
{

    private final ICommand iCommand;

    public CommandCreator(ICommand iCommand)
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
