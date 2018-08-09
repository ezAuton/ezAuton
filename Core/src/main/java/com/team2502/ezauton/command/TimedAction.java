package com.team2502.ezauton.command;

import com.team2502.ezauton.utils.ICopyableStopwatch;

public class TimedAction extends BaseAction
{

    private final double time;
    private ICopyableStopwatch stopwatch;

    public TimedAction(double time)
    {
        this.time = time;
    }

    @Override
    public void init(ICopyableStopwatch stopwatch)
    {
        this.stopwatch = stopwatch;
    }

    @Override
    public boolean isFinished()
    {
        double read = stopwatch.read();
        return read > time;
    }
}
