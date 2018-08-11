package com.team2502.ezauton.command;

import com.team2502.ezauton.utils.IClock;

public class InstantAction extends AbstractAction
{

    private final Runnable runnable;

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
