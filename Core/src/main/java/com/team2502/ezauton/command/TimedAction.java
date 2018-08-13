package com.team2502.ezauton.command;

import com.team2502.ezauton.utils.IClock;
import com.team2502.ezauton.utils.Stopwatch;
import java.util.concurrent.TimeUnit;

/**
 * Describes an action that ends after a certain amount of time has elapsed
 */
public class TimedAction extends SimpleAction
{

    private final Runnable[] runnables;
    private long millis;
    private final Stopwatch stopwatch;

    /**
     * Create a TimedAction
     *
     * @param unit      The timeunit that period is in
     * @param period    How often ro run the runnables
     * @param clock     How we are keeping time
     * @param runnables The runnables to run
     */
    public TimedAction(TimeUnit unit, long period, IClock clock, Runnable... runnables)
    {
        super(unit, period);
        millis = unit.toMillis(period);
        this.runnables = runnables;
        stopwatch = new Stopwatch(clock);
        stopwatch.resetIfNotInit();
    }

    protected void execute()
    {
        for(Runnable runnable : runnables)
        {
            runnable.run();
        }
    }


    @Override
    protected boolean isFinished()
    {
        return stopwatch.read() > millis;
    }

}
