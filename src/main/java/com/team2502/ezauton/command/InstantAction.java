package com.team2502.ezauton.command;

public class InstantAction implements IAction
{

    private final Runnable runnable;

    public InstantAction(Runnable runnable)
    {
        this.runnable = runnable;
    }

    private boolean finished = false;
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
