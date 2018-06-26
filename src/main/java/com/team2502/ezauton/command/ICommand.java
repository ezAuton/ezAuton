package com.team2502.ezauton.command;

import edu.wpi.first.wpilibj.command.Command;

public interface ICommand
{
    default void init() {}

    default void execute() {}

    boolean isFinished();

    default Command build()
    {
        return new CommandCreator(this);
    }

    default void testWith(ICommand... with)
    {
        init();
        for(ICommand iCommand : with)
        {
            iCommand.init();
        }

        boolean allFinished = false;
        while(!allFinished)
        {
            boolean notFinished = false;
            if(!isFinished())
            {
                execute();
                notFinished = true;
            }
            for(ICommand iCommand : with)
            {
                if(!iCommand.isFinished())
                {
                    iCommand.execute();
                    notFinished = true;
                }

            }
            if(!notFinished)
            {
                allFinished = true;
            }
        }
    }
}
