package com.team2502.ezauton.command;

import com.team2502.ezauton.localization.Updateable;

public class BackgroundAction extends BaseAction
{

    private final Updateable[] updateables;
    private boolean killed = false;

    public BackgroundAction(Updateable... updateables)
    {
        this.updateables = updateables;
    }

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

    public void kill()
    {
        this.killed = true;
    }
}
