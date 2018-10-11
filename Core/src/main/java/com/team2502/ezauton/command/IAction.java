package com.team2502.ezauton.command;

import com.team2502.ezauton.utils.IClock;
import com.team2502.ezauton.utils.RealClock;

import java.util.List;

/**
 * Describes an IAction, which can be thought of as a Command that can be run in a unit test
 */
public interface IAction
{
    /**
     * Run the command given a clock
     *
     * @param clock The clock to run the action
     */
    void run(IClock clock);

    /**
     * End the action peacefully
     */
    void end();

    /**
     * Returns self and runs onFinish when finished. Should not overwrite previous runnables, but instead append to list of runnables to run when finished.
     *
     * @param onFinish
     * @return
     */
    IAction onFinish(Runnable onFinish);

    List<Runnable> getFinished();

    /**
     * A helper method to schedule a real-time task. If you want other ways to schedule the action see {@link ThreadBuilder} or {@link Simulation}.
     */
    default void schedule()
    {
        new ThreadBuilder(this, RealClock.CLOCK).buildAndRun();
    }
}

