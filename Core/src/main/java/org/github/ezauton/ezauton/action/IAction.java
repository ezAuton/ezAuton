package org.github.ezauton.ezauton.action;

import org.github.ezauton.ezauton.action.simulation.MultiThreadSimulation;
import org.github.ezauton.ezauton.utils.IClock;
import org.github.ezauton.ezauton.utils.RealClock;

import java.util.List;

/**
 * Describes an IAction, which can be thought of as a Command that can be run in a unit test
 */
public interface IAction
{
    /**
     * Run the action given a clock
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
     * A helper method to schedule a real-time task. If you want other ways to schedule the action see {@link ThreadBuilder} or {@link MultiThreadSimulation}.
     */
    default void schedule()
    {
        new ThreadBuilder(this, RealClock.CLOCK).start();
    }
}

