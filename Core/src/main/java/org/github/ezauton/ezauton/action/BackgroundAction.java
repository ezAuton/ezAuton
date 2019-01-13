package org.github.ezauton.ezauton.action;

import org.github.ezauton.ezauton.localization.Updateable;

import java.util.concurrent.TimeUnit;

public class BackgroundAction extends PeriodicAction
{

    /**
     * A background action that runs forever (until manually ended with {@link IAction#end()}. Specifically uses {@link Updateable}
     * instead of {@link Runnable}, as it is meant for tasks which do simple updates.
     * not update
     * tasks wh
     *
     * @param period
     * @param timeUnit
     * @param runnables
     */
    public BackgroundAction(long period, TimeUnit timeUnit, Runnable... runnables)
    {
        super(period, timeUnit, runnables);
    }

    @Override
    protected boolean isFinished()
    {
        return false;
    }
}
