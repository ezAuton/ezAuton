package com.team2502.ezauton.command;

import com.team2502.ezauton.localization.Updateable;

import java.util.concurrent.TimeUnit;

public class BackgroundAction extends PeriodicAction
{

    /**
     * A background action that runs forever (until manually ended with {@link IAction#end()}. Specifically uses {@link Updateable}
     * instead of {@link Runnable}, as it is meant for tasks which do simple updates.
     * not update
     * tasks wh
     * @param timeUnit
     * @param period
     * @param updateables
     */
    public BackgroundAction(TimeUnit timeUnit, long period, Updateable... updateables)
    {
        super(timeUnit, period, updateables);
    }

    @Override
    protected boolean isFinished()
    {
        return false;
    }
}
