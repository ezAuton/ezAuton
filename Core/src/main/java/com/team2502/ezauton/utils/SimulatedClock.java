package com.team2502.ezauton.utils;

import java.util.List;

public class SimulatedClock implements IClock
{

    private long time = 0;

    private List<Job> jobs;

    public SimulatedClock() {}

    public void init()
    {
        init(System.currentTimeMillis());
    }

    public void init(long time)
    {
        setTime(time);
    }

    public void addTime(long dt)
    {
        setTime(time + dt);
    }

    public long getTime()
    {
        return time;
    }

    public void setTime(long time)
    {
        jobs.removeIf(job -> {
            if(job.getMillis() < time)
            {
                job.getRunnable().run();
                return true;
            }
            return false;
        });

        this.time = time;
    }

    @Override
    public long getSystemTime()
    {
        return time;
    }

    @Override
    public void scheduleAt(long millis, Runnable runnable)
    {
        if(millis < getTime())
        {
            throw new IllegalArgumentException("You are scheduling a task for before the current time!");
        }
        jobs.add(new Job(millis, runnable));
    }

    private static class Job {
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
