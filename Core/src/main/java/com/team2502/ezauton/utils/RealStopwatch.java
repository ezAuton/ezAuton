package com.team2502.ezauton.utils;

/**
 * A stopwatch that measures time in milliseconds
 */
@Deprecated //TODO: Delete -- Redundant with stopwatch ex
public class RealStopwatch extends CopyableStopwatch
{
    public RealStopwatch()
    {
        super(RealClock.CLOCK);
    }

    @Override
    public RealStopwatch copy()
    {
        RealStopwatch realStopwatch = new RealStopwatch();
        realStopwatch.millis = millis;
        return realStopwatch;
    }
}
