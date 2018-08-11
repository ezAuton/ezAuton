package com.team2502.ezauton.command;

import com.team2502.ezauton.utils.CopyableStopwatch;

/**
 * Describes an action that ends after a certain amount of time has elapsed
 */
public class TimedAction extends BaseAction
{

    private final double time;
    private CopyableStopwatch stopwatch;

    /**
     * Create a TimedAction
     *
     * @param time How many milliseconds to run for
     */
    public TimedAction(double time)
    {
        this.time = time;
    }


    @Override
    public void init(CopyableStopwatch stopwatch)
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
