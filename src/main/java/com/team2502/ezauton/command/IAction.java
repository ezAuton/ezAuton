package com.team2502.ezauton.command;

import edu.wpi.first.wpilibj.command.Command;

public interface IAction
{
    default void init() {}

    default void execute() {}

    boolean isFinished();

    /**
     * @return A WPILib command
     */
    default Command build()
    {
        return new CommandCreator(this);
    }

    /**
     * Test command (with optional other commands). If there are multiple commands, all the other
     * executes of the other commands will be run before moving on to the next iteration of calling
     * execute on this command and the rest of the commands. This is instantaneous and will generally
     * be used for simulation.
     *
     * @param with
     */
    default void testWith(IAction... with)
    {
        init();
        for(IAction iCommand : with)
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
            for(IAction iCommand : with)
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
