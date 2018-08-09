package com.team2502.ezauton.command;

/**
 * Describes an action that terminates instantly after running once
 */
public class InstantAction extends BaseAction
{

    private final Runnable runnable;
    private boolean finished = false;

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
