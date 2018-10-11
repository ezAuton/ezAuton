package org.github.ezauton.ezauton.utils;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * A clock based off of Real Clock that is warped
 */
public class TimeWarpedClock implements IClock
{
    private final double speed;
    private final RealClock realClock;
    private long startTime = 0;

    public TimeWarpedClock(double speed)
    {
        realClock = RealClock.CLOCK;
        this.speed = speed;
    }

    public double getSpeed()
    {
        return speed;
    }

    public long getStartTime()
    {
        return startTime;
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
        return (long) (dt * speed);
    }

    @Override
    public Future<?> scheduleAt(long millis, Runnable runnable)
    {
        return realClock.scheduleIn(TimeUnit.MILLISECONDS, (long) ((millis) / speed), runnable);
    }

    @Override
    public void sleep(TimeUnit timeUnit, long dt) throws InterruptedException
    {
        Thread.sleep((long) (timeUnit.toMillis(dt) / speed));
    }
}
