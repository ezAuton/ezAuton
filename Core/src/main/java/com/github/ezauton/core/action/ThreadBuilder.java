package com.github.ezauton.core.action;

import com.github.ezauton.core.utils.IClock;
import com.github.ezauton.core.utils.RealClock;

import java.util.concurrent.TimeUnit;

/**
 * Converts {@link IAction}s to {@link Thread}.
 */
public class ThreadBuilder {

    private IAction action;
    private IClock clock;
    private ToRun toRun;

    /**
     * Builds a thread to run the specified action
     *
     * @param action
     * @param clock
     */
    public ThreadBuilder(IAction action, IClock clock) {
        this.action = action;
        this.clock = clock;
        toRun = new ToRun();
    }

    /**
     * Builds a thread to run the specified action with a RealClock
     *
     * @param action
     */
    public ThreadBuilder(IAction action) {
        this(action, RealClock.CLOCK);
    }

    /**
     * Uses ForkJoinPool which will be more efficient than regular build() most of the time
     */
    public Thread build() {
        return new ActionThread(toRun);
    }

    /**
     * Get the runnable which will be put into the thread
     *
     * @return
     */
    public ToRun getToRun() {
        return toRun;
    }

    /**
     * Build a thread and start it
     *
     * @return the thread
     */
    public Thread start() {
        Thread thread = build();
        thread.start();
        return thread;
    }

    public Thread startAndWait() {
        Thread start = start();
        try {
            start.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return start;
    }

    /**
     * Starts an action and blocks for a specified time. Nice for unit testing.
     *
     * @param maxTime
     * @param unit
     * @return
     */
    public Thread startAndWait(long maxTime, TimeUnit unit) {
        Thread start = start();
        try {
            start.join(unit.toMillis(maxTime));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return start;
    }

    /**
     * Just used for instanceof
     */
    public class ActionThread extends Thread {
        public ActionThread(ToRun toRun) {
            super(toRun);
        }
    }

    private class ToRun implements Runnable {

        @Override
        public void run() {
            action.run(clock);
            action.end();
            action.getFinished().forEach(Runnable::run);
        }
    }
}
