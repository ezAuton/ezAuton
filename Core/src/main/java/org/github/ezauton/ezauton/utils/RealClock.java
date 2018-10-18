package org.github.ezauton.ezauton.utils;

import java.time.Instant;
import java.util.concurrent.*;

public class RealClock implements IClock
{
    public static final RealClock CLOCK = new RealClock();
    private final ScheduledExecutorService executorService;


    protected RealClock()
    {
        executorService = Executors.newScheduledThreadPool(1, Thread::new);
    }

    @Override
    public long getTime()
    {
        return System.currentTimeMillis();
    }

    @Override
    public Future<?> scheduleAt(long millis, Runnable runnable)
    {
        Instant scheduleTime = Instant.ofEpochMilli(millis);
        Instant now = Instant.now();

        if(scheduleTime.isBefore(now))
        {
            throw new IllegalArgumentException("You are scheduling a task for before the current time!");
        }

        return executorService.schedule(runnable, scheduleTime.toEpochMilli() - now.toEpochMilli(), TimeUnit.MILLISECONDS);
    }

    @Override
    public void sleep(long dt, TimeUnit timeUnit) throws InterruptedException
    {
        Thread.sleep(timeUnit.toMillis(dt));
    }
}
