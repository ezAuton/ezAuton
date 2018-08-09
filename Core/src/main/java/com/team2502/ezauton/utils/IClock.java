package com.team2502.ezauton.utils;

import java.util.concurrent.TimeUnit;

public interface IClock
{
    long getSystemTime();
    void scheduleAt(long millis, Runnable runnable);

    default void scheduleIn(TimeUnit timeUnit, long dt, Runnable runnable)
    {
        scheduleAt(getSystemTime()+timeUnit.toMillis(dt),runnable);
    }
}
