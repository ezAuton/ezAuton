package com.team2502.ezauton.command;

import edu.wpi.first.wpilibj.command.Command;

public interface ICommand
{
    default void init(){}
    default void execute(){}
    boolean isFinished();

    default Command build()
    {
        return new CommandCreator(this);
    }

    default void test()
    {
        init();
        while(!isFinished())
        {
            execute();
        }
    }
}
