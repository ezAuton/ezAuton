package org.github.ezauton.ezauton.action;

import org.github.ezauton.ezauton.localization.Updateable;
import org.github.ezauton.ezauton.utils.IClock;
import org.github.ezauton.ezauton.utils.Stopwatch;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class PeriodicAction extends BaseAction
{

    protected final long periodMillis;
    private final List<Updateable> updateables;
    protected IClock clock;
    protected Stopwatch stopwatch;
    protected boolean periodDelayAfterExecution = false;
    private int timesRun = 0;

    /**
     * An action which runs at recurring intervals
     *
     * @param period
     * @param timeUnit
     * @param updateables
     */
    public PeriodicAction(long period, TimeUnit timeUnit, Updateable... updateables)
    {
        this.periodMillis = timeUnit.toMillis(period);
        this.updateables = new ArrayList<>(Arrays.asList(updateables));
    }

    public PeriodicAction addUpdateable(Updateable updateable)
    {
        // Added because https://stackoverflow.com/a/9584671/4889030 is ugly
        updateables.add(updateable);
        return this;
    }

    /**
     * Creates a PeriodicAction with a 20 ms period.
     * @param updateables
     */
    public PeriodicAction(Updateable... updateables)
    {
        this(20, TimeUnit.MILLISECONDS, updateables);
    }

    public PeriodicAction()
    {
        this(20, TimeUnit.MILLISECONDS);
    }

    protected void init() {}

    protected void execute()
    {
        updateables.forEach(Updateable::update);
    }

    protected abstract boolean isFinished();

    public boolean isPeriodDelayAfterExecution()
    {
        return periodDelayAfterExecution;
    }

    public void setPeriodDelayAfterExecution(boolean periodDelayAfterExecution)
    {
        this.periodDelayAfterExecution = periodDelayAfterExecution;
    }

    @Override
    public void run(IClock clock)
    {
        this.clock = clock;

        stopwatch = new Stopwatch(clock);
        stopwatch.reset();

        Instant start = Instant.now();

        init();
        do
        {
            execute();
            Instant afterExecution = Instant.now();

            long wait;
            if(isPeriodDelayAfterExecution())
            {
                wait = periodMillis;
            }
            else
            {
                long millisTotal = Duration.between(start, afterExecution).toMillis();

                timesRun++;

                long expectedNext = periodMillis * timesRun;

                wait = expectedNext - millisTotal;
            }

            try
            {
                if(wait < 0)
                {
                    // TODO: probably should be an exception or a better way of displaying than this. (needs to be catchable though)
                    System.out.printf("The action is executing slower than the set period! milliseconds behind: %d\n", -wait);
                }
                else if (wait > 0)
                {
                    clock.sleep(wait,TimeUnit.MILLISECONDS);
                }
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
