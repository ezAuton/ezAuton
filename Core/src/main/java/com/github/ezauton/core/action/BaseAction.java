package com.github.ezauton.core.action;

import com.github.ezauton.core.utils.IClock;

import java.util.ArrayList;
import java.util.List;

/**
 * The base implementation of an IAction.
 */
public class BaseAction implements IAction
{

    private List<Runnable> toRun = new ArrayList<>();
    private boolean stopped = false;
    private Runnable runnable;

    public BaseAction()
    {

    }

    /**
     * To avoid confusion on whether {@link Runnable}s execute sequentially or in parallel, only one runnable is allowed.
     * To easily create an action with multiple {@link Runnable}s or sub actions, see {@link ActionGroup}
     *
     * @param
     */
    public BaseAction(Runnable runnable)
    {
        this.runnable = runnable;
    }

    @Override
    public void run(IClock clock)
    {
        if(runnable != null)
        {
            runnable.run();
        }
    }

    @Override
    public final IAction onFinish(Runnable onFinish)
    {
        toRun.add(onFinish);
        return this;
    }

    @Override
    public final void end()
    {
        stopped = true;
    }

    public final boolean isStopped()
    {
        return stopped;
    }

    @Override
    public final List<Runnable> getFinished()
    {
        return toRun;
    }
}
