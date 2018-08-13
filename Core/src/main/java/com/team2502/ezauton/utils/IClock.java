package com.team2502.ezauton.utils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Describes a Clock. The clock can be real or simulated. The purpose of a clock is to support a {@link Stopwatch}
 */
public interface IClock
{
    /**
     * @return The current time as read by the clock in milliseconds
     */
    long getTime();


    /**
     * Schedule an activity to run some amount of time in the future
     *
     * @param millis   The amount of milliseconds to be run in the future
     * @param runnable The thing to run
     */
    void scheduleAt(long millis, Runnable runnable);

    /**
     * Schedule a runnable to be run `dt` `timeUnit`s in the future
     * <p>
     * For example, of timeUnit is TimeUnit.MILLISECONDS and dt is 5, the runnable will be run 5 milliseconds in the future
     *
     * @param timeUnit The timeunit that dt is in
     * @param dt       The quantity of time
     * @param runnable The thing that should happen
     */
    default void scheduleIn(TimeUnit timeUnit, long dt, Runnable runnable)
    {
        scheduleAt(getTime() + timeUnit.toMillis(dt), runnable);
    }

    /**
     * Locks current thread for specified time
     *
     * @param timeUnit
     * @param dt
     */
    default void wait(TimeUnit timeUnit, long dt)
    {
        Lock lock = new ReentrantLock();
        scheduleIn(timeUnit, dt, lock::unlock);
        lock.lock();

        try
        {
            Thread.sleep(timeUnit.toMillis(dt));
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
