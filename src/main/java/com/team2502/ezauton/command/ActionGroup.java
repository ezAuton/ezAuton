package com.team2502.ezauton.command;

import com.team2502.ezauton.utils.IStopwatch;

import java.util.Arrays;
import java.util.List;

public class ActionGroup implements IAction
{
    List<IAction> actions;
    private IStopwatch stopwatch;

    public ActionGroup(IAction... actions)
    {
        this.actions = Arrays.asList(actions);
    }

    @Override
    public void init(IStopwatch stopwatch)
    {
        this.stopwatch = stopwatch;
        if(!actions.isEmpty())
        {
            stopwatch.reset();
            actions.get(0).init(stopwatch);
        }
    }

    @Override
    public void execute()
    {
        IAction action = actions.get(0);
        if(!action.isFinished())
        {
            action.execute();
        }
        else
        {
            actions.remove(0);
            if(!actions.isEmpty())
            {
                stopwatch.reset();
                actions.get(0).init(stopwatch);
            }
        }
    }

    @Override
    public boolean isFinished()
    {
        return actions.isEmpty();
    }
}
