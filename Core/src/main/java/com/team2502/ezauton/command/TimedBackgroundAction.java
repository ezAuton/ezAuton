package com.team2502.ezauton.command;

import com.team2502.ezauton.localization.Updateable;

import java.util.concurrent.TimeUnit;

/**
 * Describes an action that ends after a certain amount of time has elapsed
 */
public class TimedBackgroundAction extends BackgroundAction
{

    private long durationMillis;

    /**
     * @param periodUnit
     * @param period       How long to (repeatedly) run the runnables for
     * @param durationUnit
     * @param duration     The timeunit that period is
     * @param updateables
     */
    public TimedBackgroundAction(TimeUnit periodUnit, long period, TimeUnit durationUnit, long duration, Updateable... updateables)
    {
        super(periodUnit, period, updateables);
        durationMillis = durationUnit.toMillis(duration);
    }


    @Override
    protected boolean isFinished()
    {
        return this.stopwatch != null && this.stopwatch.read() > durationMillis;
    }

}
