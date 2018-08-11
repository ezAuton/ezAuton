package com.team2502.ezauton.utils;

import java.util.concurrent.TimeUnit;

/**
 * A handy stopwatch for recording time in seconds every time it is polled
 *
 * @deprecated Use {@link ICopyable}
 */
public class Stopwatch
{

    protected final IClock clock;
    protected long millis = -1;

    public Stopwatch(IClock clock)
    {
        this.clock = clock;
    }

    public void init()
    {
        millis = clock.getTime();

    }

    /**
     * Read and reset
     *
     * @return The value of the stopwatch
     */
    public double pop()
    {
        double readVal = read();
        reset();
        return readVal;
    }

    /**
     * Read without resetting
     *
     * @return The value of the stopwatch
     */
    public double read()
    {
        return clock.getTime() - millis;
    }

    /**
     * Reset without reading
     */
    public void reset()
    {
        millis = clock.getTime();
    }

    /**
     * @return If this stopwatch is initialized
     */
    public boolean isInit()
    {
        return millis != -1;
    }

    /**
     * @return If is not init
     */
    public boolean resetIfNotInit()
    {
        if(isInit())
        {
            return false;
        }
        reset();
        return true;
    }

    /**
     * Locks current thread for the time specified
     *
     * @param amount
     * @param timeUnit
     *
     * @throws InterruptedException
     */
    public void wait(int amount, TimeUnit timeUnit) throws InterruptedException
    {
        Thread.sleep(timeUnit.toMillis(amount));
    }
}
