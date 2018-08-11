package com.team2502.ezauton.command;

import com.team2502.ezauton.utils.ICopyable;
import com.team2502.ezauton.utils.RealStopwatch;

import java.util.ArrayList;
import java.util.List;

/**
 * Describes an IAction with some boilerplate built in, such as a stopwatch
 */
public abstract class BaseAction implements IAction
{
    protected boolean used = false;
    private ICopyable stopwatch;
    private List<Runnable> runnables = new ArrayList<>();

    /**
     * Initialize this BaseAction. Runs once.
     *
     * @param stopwatch The stopwatch we should use to keep track of time
     */
    protected void init(ICopyable stopwatch)
    {
        this.stopwatch = stopwatch;
    }

    public ICopyable getStopwatch()
    {
        return stopwatch;
    }

    /**
     * Runs continually until isFinished returns true
     */
    protected void execute() {}

    /**
     * @return Whether or not this IAction has finished
     */
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

    /**
     * Add something to do when finished
     *
     * @param onFinish The thing to do when finished
     * @return this
     */
    @Override
    public BaseAction onFinish(Runnable onFinish)
    {
        runnables.add(onFinish);
        return this;
    }

    /**
     * Simulate this action
     * @param millisPeriod How often {@link BaseAction#execute()} should be called
     */
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

    /**
     * Stop simulating this action
     */
    @Override
    public void removeSimulator()
    {
        SimulatorManager.getInstance().remove(this);
    }

}
