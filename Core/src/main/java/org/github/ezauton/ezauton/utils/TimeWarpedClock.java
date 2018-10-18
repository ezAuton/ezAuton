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
    private long startTime = 0;

    public TimeWarpedClock(double speedMultiplier)
    {
        realClock = RealClock.CLOCK;
        this.speedMultiplier = speedMultiplier;
    }

    public double getSpeed()
    {
        return speedMultiplier;
    }

    public long getStartTime()
    {
        return startTime;
    }

    public void setStartTimeNow()
    {
        startTime = System.currentTimeMillis();
    }

    /**
     * Sets the start time in milliseconds
     *
     * @param startTime
     */
    public void setStartTime(long startTime)
    {
        this.startTime = startTime;
    }

    @Override
    public long getTime()
    {
        long dt = System.currentTimeMillis() - startTime;
        return (long) (dt * speedMultiplier);
    }

    @Override
    public Future<?> scheduleAt(long millis, Runnable runnable)
    {
        return realClock.scheduleIn((long) ((millis) / speedMultiplier), TimeUnit.MILLISECONDS, runnable);
    }

    @Override
    public void sleep(long dt, TimeUnit timeUnit) throws InterruptedException
    {
        Thread.sleep((long) (timeUnit.toMillis(dt) / speedMultiplier));
    }
}
