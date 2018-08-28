package com.team2502.ezauton.command;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractAction implements IAction
{

    private List<Runnable> toRun = new ArrayList<>();
    private boolean stopped = false;

    @Override
    public IAction onFinish(Runnable onFinish)
    {
        toRun.add(onFinish);
        return this;
    }

    @Override
    public void end()
    {
        stopped = true;
    }

    public boolean isStopped()
    {
        return stopped;
    }

    @Override
    public List<Runnable> getFinished()
    {
        return toRun;
    }
}
