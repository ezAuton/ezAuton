package com.team2502.ezauton.utils;

import com.team2502.ezauton.localization.Updateable;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Each time progress() is called, SimulatedStopwatch increases by dt
 */
public class SimulatedStopwatch extends CopyableStopwatch implements Updateable
{

    private final double dt;
    Set<SimulatedStopwatch> related = new HashSet<>();
    double sum = -1;

    public SimulatedStopwatch(SimulatedClock clock, double dt)
    {
        super(clock);
        this.dt = dt;
    }

    @Override
    public double read()
    {
        return sum;
    }

    /**
     * Make time move forward by dt milliseconds
     */
    public void progress()
    {
        progress(dt);
    }

    /**
     * Make time move forward by some number of milliseconds
     *
     * @param dt How many milliseconds to move forward
     */
    public void progress(double dt)
    {
        related.forEach(simulatedStopwatch -> simulatedStopwatch.progress(dt));
        if(sum == -1)
        {
            sum = 0;
        }
        sum += dt;

    }

    @Override
    public void reset()
    {
        sum = 0;
    }

    @Override
    public boolean isInit()
    {
        return sum != -1;
    }


    @Override
    public void wait(int amount, TimeUnit timeUnit)
    {

    }

    /**
     * @return A new SimulatedStopwatch that is linked to progress()
     */
    @Override
    public SimulatedStopwatch copy()
    {
        SimulatedStopwatch simulatedStopwatch = new SimulatedStopwatch((SimulatedClock) clock, dt);
        simulatedStopwatch.sum = sum;

        related.add(simulatedStopwatch);

        return simulatedStopwatch;
    }

    @Override
    public boolean update()
    {
        progress();
        return true;
    }
}
