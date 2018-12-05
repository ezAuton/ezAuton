package org.github.ezauton.ezauton.utils;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * A clock based off of Real Clock that is warped
 */
public class TimeWarpedClock implements IClock
{
    private final double speedMultiplier;
    private final RealClock realClock;
    private long startTime;
    private long timeStartedAt;

    public TimeWarpedClock(double speedMultiplier, long startTime)
    {
        realClock = RealClock.CLOCK;
        this.speedMultiplier = speedMultiplier;
        this.startTime = startTime;
        timeStartedAt = System.currentTimeMillis();
    }

    public TimeWarpedClock(double speedMultiplier)
    {
        this(speedMultiplier, System.currentTimeMillis());
    }

    public double getSpeed()
    {
        return speedMultiplier;
    }

    public long getStartTime()
    {
        return startTime;
    }

    @Override
    public long getTime()
    {
        long realDt = realClock.getTime() - timeStartedAt;
        return (long) (realDt * speedMultiplier + startTime);
    }

    @Override
    public Future<?> scheduleAt(long millis, Runnable runnable)
    {
        double realDt = (millis - getTime()) / speedMultiplier;
        System.out.println(realDt);
        return realClock.scheduleIn((long) realDt, TimeUnit.MILLISECONDS, runnable);
    }

    @Override
    public void sleep(long dt, TimeUnit timeUnit) throws InterruptedException
    {
        Thread.sleep((long) (timeUnit.toMillis(dt) / speedMultiplier));
    }
}
