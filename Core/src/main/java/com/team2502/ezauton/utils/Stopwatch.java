package com.team2502.ezauton.utils;

import java.util.concurrent.TimeUnit;

/**
 * A handy stopwatch for recording time in seconds every time it is polled
 *
 * @deprecated Use {@link ICopyableStopwatch}
 */
public class Stopwatch
{

    private final IClock clock;
    long millis = 0;

    public Stopwatch(IClock clock)
    {
        this.clock = clock;
    }

    public void init()
    {

    }

    /**
     * Read and reset
     *
     * @return The value of the stopwatch
     */
    default double pop()
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
    double read();

    /**
     * Reset without reading
     */
    void reset()
    {

    }

    /**
     * @return If this stopwatch is initialized
     */
    boolean isInit();

    /**
     * @return If is not init
     */
    default boolean resetIfNotInit()
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
     * @param amount
     * @param timeUnit
     */
    void wait(int amount, TimeUnit timeUnit);
}
