package com.team2502.ezauton.command;

import com.team2502.ezauton.localization.Updateable;

/**
 * Describes an action that continually updates Updateables until killed
 *
 * This is meant to be run in the background i.e as a parallel action
 */
public class BackgroundAction extends BaseAction //TODO: Rename to UpdaterAction?
{

    private final Updateable[] updateables;
    private boolean killed = false;

    /**
     * Create a BackgroundAction
     *
     * @param updateables The updateables to update
     */
    public BackgroundAction(Updateable... updateables)
    {
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
        this.killed = true;
    }
}
