package org.github.ezauton.ezauton.utils;

public abstract class CopyableStopwatch extends Stopwatch implements ICopyable
{
    public CopyableStopwatch(IClock clock)
    {
        super(clock);
    }
}
