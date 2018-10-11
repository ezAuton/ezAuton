package com.team2502.ezauton.command;

import com.team2502.ezauton.utils.IClock;

/**
 * Describes an action that runs instantly
 */
public class QuickAction extends BaseAction
{

    private final Runnable runnable;

    /**
     * Create an QuickAction
     *
     * @param runnable The thing to run
     */
    public QuickAction(Runnable runnable)
    {
        this.runnable = runnable;
    }

    @Override
    public void run(IClock clock)
    {
        runnable.run();
    }
}
