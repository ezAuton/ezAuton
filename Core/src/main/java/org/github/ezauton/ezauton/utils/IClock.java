package org.github.ezauton.ezauton.utils;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Describes a Clock. The clock can be real or simulated. The purpose of a clock is to support a {@link Stopwatch}
 */
public interface IClock
{
    /**
     * @return The current time as read by the clock in seconds
     */
    long getTime();


    /**
     * Schedule an activity to run some amount of time in the future
     *
     * Should, if possible, be FIFO
     *
     * @param millis   The timestamp at which the runnable should be run
     * @param runnable The thing to run
     */
    void scheduleAt(long millis, Runnable runnable);

    /**
     * Schedule a runnable to be run `dt` `timeUnit`s in the future
     * <p>
     * For example, of timeUnit is TimeUnit.MILLISECONDS and dt is 5, the runnable will be run 5 milliseconds in the future
     *
     * Should, if possible, be FIFO
     *
     * @param dt       The quantity of time
     * @param timeUnit The timeunit that dt is in
     * @param runnable The thing that should happen
     */
    default void scheduleIn(long dt, TimeUnit timeUnit, Runnable runnable)
    {
        scheduleAt(getTime() + timeUnit.toMillis(dt), runnable);
    }

    default void scheduleNow(Runnable runnable)
    {
        scheduleAt(getTime(), runnable);
    }

    /**
     * Locks current thread for specified time
     *
     * @param dt
     * @param timeUnit
     */
    void sleep(long dt, TimeUnit timeUnit) throws InterruptedException;
}
