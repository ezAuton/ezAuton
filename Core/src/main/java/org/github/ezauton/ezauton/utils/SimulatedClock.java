package org.github.ezauton.ezauton.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @deprecated old af and half-working. Use {@link org.github.ezauton.ezauton.action.simulation.ModernSimulatedClock}
 */
public class SimulatedClock implements IClock
{

    private long time = 0;

    private List<Job> jobs = new ArrayList<>();

    public SimulatedClock() {}

    public void init()
    {
        init(System.currentTimeMillis());
    }

    public void init(long time)
    {
        setTime(time);
    }

    /**
     * Add time in milliseconds
     *
     * @param dt millisecond increase
     * @return The new time
     */
    public long addTime(long dt)
    {
        setTime(getTime() + dt);
        return getTime();
    }

    /**
     * Adds time with units
     *
     * @param value
     * @param timeUnit
     */
    public void addTime(long value, TimeUnit timeUnit)
    {
        addTime(timeUnit.toMillis(value));
    }

    /**
     * Add one millisecond and returns new value
     *
     * @return The new time
     */
    public long incAndGet()
    {
        return addTime(1);
    }

    /**
     * Increment a certain amount of times
     *
     * @param times
     */
    public void incTimes(long times, long dt)
    {
        long init = getTime();
        long totalDt = times * dt;
        for(int i = 0; i < times; i++)
        {
            if(!jobs.isEmpty())
            {
                addTime(dt);
            }
            else
            {
                break;
            }
        }
        setTime(init + totalDt);
    }

    /**
     * Increment a certain amount of times
     *
     * @param times
     * @return
     */
    public void incTimes(long times)
    {
        incTimes(times, 1);
    }

    @Override
    public long getTime()
    {
        return time;
    }

    public void setTime(long time)
    {
        synchronized(this)
        {
            jobs.removeIf(job -> {
                if(job.getMillis() < time)
                {
                    job.getRunnable().run();
                    return true;
                }
                return false;
            });

            notifyAll();
            this.time = time;
        }
    }

    /**
     * @param millis   The amount of milliseconds to be run in the future
     * @param runnable The thing to run
     * @return
     * @deprecated Does not currently return a Future
     */
    @Override
    public void scheduleAt(long millis, Runnable runnable)
    {
        if(millis < getTime())
        {
            throw new IllegalArgumentException("You are scheduling a task for before the current time!");
        }
        jobs.add(new Job(millis, runnable));
    }

    @Override
    public void sleep(long dt, TimeUnit timeUnit) throws InterruptedException
    {
        long startTime = getTime();
        while(startTime + timeUnit.toMillis(dt) < getTime())
        {
            wait();
        }
    }

    private static class Job
    {
        private final long millis;
        private final Runnable runnable;

        public Job(long millis, Runnable runnable)
        {
            this.millis = millis;
            this.runnable = runnable;
        }

        public long getMillis()
        {
            return millis;
        }

        public Runnable getRunnable()
        {
            return runnable;
        }
    }
}
