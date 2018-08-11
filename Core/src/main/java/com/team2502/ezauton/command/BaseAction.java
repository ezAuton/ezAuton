package com.team2502.ezauton.command;

import com.team2502.ezauton.utils.ICopyable;
import com.team2502.ezauton.utils.RealStopwatch;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseAction implements IAction
{
    protected boolean used = false;
    private ICopyable stopwatch;
    private List<Runnable> runnables = new ArrayList<>();

    protected void init(ICopyable stopwatch)
    {
        this.stopwatch = stopwatch;
    }

    public ICopyable getStopwatch()
    {
        return stopwatch;
    }

    protected void execute() {}

    protected abstract boolean isFinished();

    public List<Runnable> getRunnables()
    {
        return runnables;
    }

    /**
     * @return A Thread which acts like a command but can be run faster
     */
    @Override
    public Thread buildThread(long millisPeriod)
    {
        if(used)
        {
            throw new RuntimeException("Action already used!");
        }
        used = true;
        return new Thread(() -> {
            RealStopwatch stopwatch = new RealStopwatch();
            init(stopwatch);
            while(!isFinished())
            {
                execute();
                try
                {
                    Thread.sleep(millisPeriod);
                }
                catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            runnables.forEach(Runnable::run);
        });
    }

    @Override
    public BaseAction onFinish(Runnable onFinish)
    {
        runnables.add(onFinish);
        return this;
    }

    @Override
    public void simulate(long millisPeriod)
    {
        if(used)
        {
            throw new RuntimeException("Action already used!");
        }
        used = true;
        SimulatorManager.getInstance().schedule(this, millisPeriod);
    }

    @Override
    public void removeSimulator()
    {
        SimulatorManager.getInstance().remove(this);
    }

}
