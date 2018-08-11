package com.team2502.ezauton.utils;

public abstract class CopyableStopwatch extends Stopwatch implements ICopyable
{
    public CopyableStopwatch(IClock clock)
    {
        super(clock);
    }
}
