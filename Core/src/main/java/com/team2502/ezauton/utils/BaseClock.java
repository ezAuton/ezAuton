package com.team2502.ezauton.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BaseClock implements IClock
{


    private final ScheduledExecutorService executorService;

    public BaseClock()
    {
        executorService = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public long getTime()
    {
        return 0;
    }

    @Override
    public Future<?> scheduleAt(long millis, Runnable runnable)
    {
        return null;
    }

    @Override
    public void sleep(TimeUnit timeUnit, long dt) throws InterruptedException
    {

    }
}
