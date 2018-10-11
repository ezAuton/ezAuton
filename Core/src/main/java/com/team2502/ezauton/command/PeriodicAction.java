package com.team2502.ezauton.command;

import com.team2502.ezauton.utils.IClock;
import com.team2502.ezauton.utils.Stopwatch;

import java.util.concurrent.TimeUnit;

public abstract class PeriodicAction extends BaseAction
{

    protected final long periodMillis;
    protected IClock clock;
    protected Stopwatch stopwatch;

    public PeriodicAction(TimeUnit timeUnit, long period)
    {
        this.periodMillis = timeUnit.toMillis(period);
    }

    protected void init() {}

    protected void execute() {}

    protected abstract boolean isFinished();

    @Override
    public void run(IClock clock)
    {
        this.clock = clock;

        stopwatch = new Stopwatch(clock);
        stopwatch.reset();

        init();
        do
        {
            execute();
            try
            {
                clock.sleep(TimeUnit.MILLISECONDS, periodMillis);
            }
            catch(InterruptedException e)
            {
                return;
            }
        }
        while(!isFinished() && !isStopped());
    }

    public Stopwatch getStopwatch()
    {
        return stopwatch;
    }

    public IClock getClock()
    {
        return clock;
    }

}
