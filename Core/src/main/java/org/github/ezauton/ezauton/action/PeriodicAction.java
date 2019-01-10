package org.github.ezauton.ezauton.action;

import org.github.ezauton.ezauton.localization.Updateable;
import org.github.ezauton.ezauton.utils.IClock;
import org.github.ezauton.ezauton.utils.Stopwatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public abstract class PeriodicAction extends BaseAction
{

    protected final long periodMillis;
    private final List<Runnable> runnables;
    protected IClock clock;
    protected Stopwatch stopwatch;
    protected boolean periodDelayAfterExecution = false;
    private int timesRun = 0;

    /**
     * An action which runs at recurring intervals
     *
     * @param period
     * @param timeUnit
     * @param runnables
     */
    public PeriodicAction(long period, TimeUnit timeUnit, Runnable... runnables)
    {
        this.periodMillis = timeUnit.toMillis(period);
        this.runnables = new ArrayList<>(Arrays.asList(runnables));
    }

    /**
     * Creates a PeriodicAction with a 20 ms period.
     *
     * @param runnables
     */
    public PeriodicAction(Runnable... runnables)
    {
        this(20, TimeUnit.MILLISECONDS, runnables);
    }

    public PeriodicAction()
    {
        this(20, TimeUnit.MILLISECONDS);
    }

    public PeriodicAction addUpdateable(Runnable runnable)
    {
        // Added because https://stackoverflow.com/a/9584671/4889030 is ugly
        runnables.add(runnable);
        return this;
    }

    public int getTimesRun() {
        return timesRun;
    }

    /**
     * Added because https://stackoverflow.com/a/9584671/4889030 is ugly
     *
     * @param updateableFunc
     * @return
     */
    public PeriodicAction addUpdateable(Function<PeriodicAction, Runnable> updateableFunc)
    {
        runnables.add(updateableFunc.apply(this));
        return this;
    }

    protected void init() {}

    protected void execute()
    {
        runnables.forEach(Runnable::run);
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
    public final void run(IClock clock)
    {
        this.clock = clock;

        stopwatch = new Stopwatch(clock);
        stopwatch.reset();

        long start = clock.getTime();

        init();
        do
        {
            execute();
            long afterExecution = clock.getTime();

            long wait;
            if(isPeriodDelayAfterExecution())
            {
                wait = periodMillis;
            }
            else
            {
                long millisTotal = afterExecution - start;

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
                else if(wait > 0)
                {
                    clock.sleep(wait, TimeUnit.MILLISECONDS);
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
