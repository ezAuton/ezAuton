package com.team2502.ezauton.command;

import com.team2502.ezauton.utils.IClock;

import java.util.concurrent.TimeUnit;

/**
 * Describes an action that waits a certain amount of time before running
 */
public class DelayedAction extends BaseAction
{

    private Runnable runnable;
    private long millis;

    public DelayedAction(TimeUnit unit, long value)
    {
        millis = unit.toMillis(value);
    }

    public DelayedAction(TimeUnit unit, long value, Runnable runnable)
    {
        this(unit,value);
        this.runnable = runnable;
    }

    /**
     * Called after delay is done
     */
    public void onTimeUp(IClock clock)
    {
        if(runnable != null)
        {
            runnable.run();
        }
    }

    /**
     * Do not override!
     * @param clock
     */
    @Override
    public void run(IClock clock)
    {
        try
        {
            clock.sleep(TimeUnit.MILLISECONDS, millis);
        }
        catch(InterruptedException e)
        {
            return;
        }

        if(isStopped())
        {
            return;
        }
        onTimeUp(clock);
    }
}