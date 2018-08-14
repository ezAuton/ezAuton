package com.team2502.ezauton.utils;

import java.util.concurrent.TimeUnit;

/**
 * A handy stopwatch for recording time in seconds every time it is polled
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
     * @return The value of the stopwatch (ms)
     */
    public double pop()
    {
        double readVal = read();
        reset();
        return readVal;
    }

    /**
     * Read and reset
     *
     * @param timeUnit The time unit you would like to get the result in
     * @return Value of stopwatch (in specified timeunit)
     */
    public double pop(TimeUnit timeUnit)
    {
        return pop() / timeUnit.toMillis(1);
    }

    public IClock getClock()
    {
        return clock;
    }

    /**
     * Read without resetting
     *
     * @return The value of the stopwatch (ms)
     */
    public double read()
    {
        return clock.getTime() - millis;
    }

    public double read(TimeUnit timeUnit)
    {
        return read() / timeUnit.toMillis(1);
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
}
