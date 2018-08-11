package com.team2502.ezauton.command;

import com.team2502.ezauton.utils.IClock;
import com.team2502.ezauton.utils.RealClock;

import java.util.concurrent.ForkJoinPool;

public class ThreadBuilder
{

    private IAction action;
    private IClock clock;
    private ToRun toRun;

    /**
     * Builds a thread to run the specified action
     *
     * @param action
     * @param clock
     */
    public ThreadBuilder(IAction action, IClock clock)
    {
        this.action = action;
        this.clock = clock;
        toRun = new ToRun();
    }

    /**
     * Builds a thread to run the specified action with a RealClock
     *
     * @param action
     */
    public ThreadBuilder(IAction action)
    {
        this(action, RealClock.CLOCK);
    }

    /**
     * Builds a new thread
     *
     * @return
     */
    public Thread build()
    {
        return new Thread(toRun);
    }

    /**
     * Uses ForkJoinPool which will be more efficient than regular build() most of the time
     */
    public void buildAndRun()
    {
        ForkJoinPool.commonPool().execute(toRun);
    }

    private class ToRun implements Runnable
    {

        @Override
        public void run()
        {
            action.run(clock);
            action.getFinished().forEach(Runnable::run);
        }
    }
}
