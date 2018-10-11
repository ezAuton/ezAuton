package com.team2502.ezauton.command;

import com.team2502.ezauton.localization.Updateable;
import com.team2502.ezauton.utils.IClock;
import com.team2502.ezauton.utils.Stopwatch;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public abstract class PeriodicAction extends BaseAction
{

    protected final long periodMillis;
    private final Updateable[] updateables;
    protected IClock clock;
    protected Stopwatch stopwatch;

    /**
     * An action which runs at recurring intervals
     * @param timeUnit
     * @param period
     * @param updateables
     */
    public PeriodicAction(TimeUnit timeUnit, long period, Updateable... updateables)
    {
        this.periodMillis = timeUnit.toMillis(period);
        this.updateables = updateables;
    }

    protected void init() {}

    protected void execute() {
        Arrays.stream(updateables).forEach(Updateable::update);
    }

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
