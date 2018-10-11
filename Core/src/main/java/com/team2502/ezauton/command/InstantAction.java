package com.team2502.ezauton.command;

import com.team2502.ezauton.utils.IClock;

/**
 * Describes an action that runs instantly
 */
public class InstantAction extends BaseAction
{

    private final Runnable runnable;

    /**
     * Create an InstantAction
     *
     * @param runnable The thing to run
     */
    public InstantAction(Runnable runnable)
    {
        this.runnable = runnable;
    }

    @Override
    public void run(IClock clock)
    {
        runnable.run();
    }
}
