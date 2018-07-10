package com.team2502.ezauton.command;

public class InstantAction extends BaseAction
{

    private final Runnable runnable;
    private boolean finished = false;

    public InstantAction(Runnable runnable)
    {
        this.runnable = runnable;
    }

    @Override
    public void execute()
    {
        runnable.run();
        finished = true;
    }

    @Override
    public boolean isFinished()
    {
        return finished;
    }
}
