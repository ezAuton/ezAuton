package com.team2502.ezauton.test.utils;

/**
 * A handy stopwatch for recording time in seconds every time it is polled
 */
public interface IStopwatch
{
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
    void reset();

    /**
     * @return If this stopwatch is initialized
     */
    boolean isInit();
}
