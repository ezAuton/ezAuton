package com.team2502.ezauton.command;

import com.team2502.ezauton.localization.Updateable;

import java.util.concurrent.TimeUnit;

public class BackgroundAction extends PeriodicAction
{

    private final Updateable[] updateables;

    public BackgroundAction(TimeUnit timeUnit, long period, Updateable... updateables)
    {
        super(timeUnit, period);
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
    protected boolean isFinished()
    {
        return false;
    }
}
