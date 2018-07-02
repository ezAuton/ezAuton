package com.team2502.ezauton.command;

public abstract class TimedAction implements IAction
{

    private final double time;

    public TimedAction(double time)
    {
        this.time = time;
    }


}
