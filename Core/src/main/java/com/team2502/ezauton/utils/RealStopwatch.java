package com.team2502.ezauton.utils;

/**
 * A stopwatch that measures time in milliseconds
 */
@Deprecated //TODO: Delete -- Redundant with stopwatch ex
public class RealStopwatch extends Stopwatch implements ICopyable
{
    public RealStopwatch(IClock clock)
    {
        super(clock);
    }

    @Override
    public RealStopwatch copy()
    {
        RealStopwatch realStopwatch = new RealStopwatch((RealClock) clock);
        realStopwatch.millis = millis;
        return realStopwatch;
    }
}
