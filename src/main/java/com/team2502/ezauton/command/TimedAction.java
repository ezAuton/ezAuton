package com.team2502.ezauton.command;

import com.team2502.ezauton.utils.IStopwatch;

public abstract class TimedAction implements IAction
{

    private final double time;
    private IStopwatch stopwatch;

    public TimedAction(double time)
    {
        this.time = time;
    }

    @Override
    public void init(IStopwatch stopwatch)
    {
        this.stopwatch = stopwatch;
    }

    @Override
    public boolean isFinished()
    {
        return stopwatch.read() > time;
    }
}
