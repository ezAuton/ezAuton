package com.team2502.ezauton.utils;

/**
 * A stopwatch that measures time in milliseconds
 */
public class RealStopwatch implements ICopyableStopwatch
{
    /**
     * Time in nanoseconds since last {@link RealStopwatch#reset()}
     */
    private long lastTime;

    public RealStopwatch()
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

    @Override
    public RealStopwatch copy()
    {
        RealStopwatch realStopwatch = new RealStopwatch();
        realStopwatch.lastTime = lastTime;
        return realStopwatch;
    }
}
