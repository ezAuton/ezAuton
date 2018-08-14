package com.team2502.ezauton.command;

import com.team2502.ezauton.utils.IClock;

import java.util.List;

public interface IAction
{
    /**
     * Run the command given a clock
     *
     * @param clock The clock to run the action
     */
    void run(IClock clock);

    void stop();

    /**
     * Returns self and runs onFinish when finished. Should not overwrite previous runnables, but instead append to list of runnables to run when finished.
     *
     * @param onFinish
     * @return
     */
    IAction onFinish(Runnable onFinish);

    List<Runnable> getFinished();

}

