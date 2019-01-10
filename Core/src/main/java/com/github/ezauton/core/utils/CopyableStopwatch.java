package com.github.ezauton.core.utils;

public abstract class CopyableStopwatch extends Stopwatch implements ICopyable
{
    public CopyableStopwatch(IClock clock)
    {
        super(clock);
    }
}
