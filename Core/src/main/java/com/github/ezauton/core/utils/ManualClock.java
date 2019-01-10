package com.github.ezauton.core.utils;

import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class ManualClock implements IClock
{

    private long time = 0;

    private final TreeMap<Long, Queue<Runnable>> timeToRunnableMap = new TreeMap<>();


    public ManualClock() {}

    public void init()
    {
        init(System.currentTimeMillis());
    }

    public void init(long time)
    {
        setTime(time);
    }

    /**
     * Add time in milliseconds
     *
     * @param dt millisecond increase
     * @return The new time
     */
    public long addTime(long dt)
    {
        setTime(getTime() + dt);
        return getTime();
    }

    /**
     * Adds time with units
     *
     * @param value
     * @param timeUnit
     */
    public void addTime(long value, TimeUnit timeUnit)
    {
        addTime(timeUnit.toMillis(value));
    }

    /**
     * Add one millisecond and returns new value
     *
     * @return The new time
     */
    public long incAndGet()
    {
        return addTime(1);
    }

    @Override
    public long getTime()
    {
        return time;
    }

    public void setTime(long time)
    {
        while (!timeToRunnableMap.isEmpty() && timeToRunnableMap.firstKey() <= time)
        {
            Map.Entry<Long, Queue<Runnable>> entry = timeToRunnableMap.pollFirstEntry();
            Queue<Runnable> queue = entry.getValue();
            queue.removeIf(runnable -> {
                runnable.run();
                return true; // TODO: is this good code style?
            });
        }
        this.time = time;
    }

    /**
     * @param millis   The amount of milliseconds to be run in the future
     * @param runnable The thing to run
     * @return
     * @deprecated Does not currently return a Future
     */
    @Override
    public void scheduleAt(long millis, Runnable runnable)
    {
        if(millis < getTime())
        {
            throw new IllegalArgumentException("You are scheduling a task for before the current time!");
        }
        if(millis == getTime()) runnable.run();
    }

    @Override
    public void sleep(long dt, TimeUnit timeUnit) throws InterruptedException
    {
        long startTime = getTime();
        while(startTime + timeUnit.toMillis(dt) < getTime())
        {
            wait();
        }
    }
}
