package com.team2502.ezauton.utils;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Describes a clock that runs faster than RealClock
 */
public class FastClock implements IClock
{

    private final double scaleFactor;
    private long initMillis;

    /**
     * Create a fastclock
     *
     * @param scaleFactor The scale factor. If it is 10, then 10 milliseconds according to this clock will be 1 real millisecond.
     */
    public FastClock(double scaleFactor)
    {
        this.scaleFactor = scaleFactor;
        this.initMillis = System.currentTimeMillis();
    }

    @Override
    public long getTime()
    {
        long dt = System.currentTimeMillis() - initMillis;
        return (long) (dt * scaleFactor);
    }

    @Override
    public void scheduleAt(long millis, Runnable runnable)
    {
        Timer timer = new Timer();
        if (millis < getTime())
        {
            throw new IllegalArgumentException("You are scheduling a task for before the current time!");
        }
        long delay = (long) ((millis - getTime()) / scaleFactor);
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                runnable.run();
            }
        }, delay);
    }


    @Override
    public void wait(TimeUnit timeUnit, long dt)
    {

    }
}
