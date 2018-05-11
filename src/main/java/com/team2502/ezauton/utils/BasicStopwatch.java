package com.team2502.ezauton.utils;

/**
 * A stopwatch that measures time in milliseconds
 */
public class BasicStopwatch implements IStopwatch
{
    /**
     * Time in nanoseconds since last {@link BasicStopwatch#reset()}
     */
    private long lastTime;

    public BasicStopwatch()
    {
        lastTime = System.nanoTime();
    }

    @Override
    public double read() // returns milliseconds
    {
        return (System.nanoTime() - lastTime) * 1E-6;
    }

    @Override
    public void reset()
    {
        lastTime = System.nanoTime();
    }

    @Override
    public boolean isInit()
    {
        return lastTime != -1;
    }
}
