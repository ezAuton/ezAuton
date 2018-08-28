package com.team2502.ezauton.command;

import com.team2502.ezauton.utils.IClock;

import java.util.concurrent.TimeUnit;

public class DelayedAction extends AbstractAction
{

    private final Runnable[] runnables;
    private long millis;

    public DelayedAction(TimeUnit unit, long value, Runnable... runnables)
    {
        millis = unit.toMillis(value);
        this.runnables = runnables;
    }

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
        for(Runnable runnable : runnables)
        {
            runnable.run();
        }
    }
}
