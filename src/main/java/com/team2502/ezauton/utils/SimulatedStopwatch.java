package com.team2502.ezauton.utils;

/**
 * Each time progress() is called, SimulatedStopwatch increases by dt
 */
public class SimulatedStopwatch implements IStopwatch
{

    private final double dt;
    float count = -1;

    public SimulatedStopwatch(double dt)
    {
        this.dt = dt;
    }

    @Override
    public double read()
    {
        return count;
    }

    /**
     *  progress to next time step
     */
    public void progress()
    {
        if(count == -1)
        {
            count = 0;
        }
        count += dt;
    }

    /**
     *  progress to next time step
     */
    public void progress(double dt)
    {
        if(count == -1)
        {
            count = 0;
        }
        count += dt;
    }

    @Override
    public void reset()
    {
        count = 0;
    }

    @Override
    public boolean isInit()
    {
        return count != -1;
    }

    @Override
    public SimulatedStopwatch clone()
    {
        SimulatedStopwatch simulatedStopwatch = new SimulatedStopwatch(dt);
        simulatedStopwatch.count = count;
        return simulatedStopwatch;
    }
}
