package com.team2502.ezauton.command;

import com.team2502.ezauton.localization.Updateable;

import java.util.concurrent.TimeUnit;

/**
 * Describes an action that continually updates Updateables until killed
 *
 * This is meant to be run in the background i.e as a parallel action
 */
public class BackgroundAction extends SimpleAction
{

    private final Updateable[] updateables;
    private boolean killed;

    /**
     * Create a BackgroundAction
     *
     * @param timeUnit    The timeunit that period is in
     * @param period      How often to update our updateables
     * @param updateables The updateables to update
     */
    public BackgroundAction(TimeUnit timeUnit, long period, Updateable... updateables)
    {
        super(timeUnit, period);
        this.updateables = updateables;
    }

    /**
     * Update our updateables
     */
    @Override
    public void execute()
    {
        for(Updateable updateable : updateables)
        {
            updateable.update();
        }
    }

    @Override
    public boolean isFinished()
    {
        return killed;
    }

    public boolean isKilled()
    {
        return killed;
    }

    /**
     * Prevents this BackgroundAction from continuing to run.
     */
    public void kill()
    {
        killed = true;
    }
}
