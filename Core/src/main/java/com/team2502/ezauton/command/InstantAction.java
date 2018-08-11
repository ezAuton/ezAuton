package com.team2502.ezauton.command;

import com.team2502.ezauton.utils.IClock;

/**
 * Describes an action that terminates instantly after running once
 */
public class InstantAction extends AbstractAction
{

    private final Runnable runnable;

    /**
     * Create an InstantAction
     *
     * @param runnable The thing to do
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
