package com.team2502.ezauton.command;


import com.team2502.ezauton.utils.IClock;
import com.team2502.ezauton.utils.TimeWarpedClock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class Simulation
{

    private final double speed;
    private final TimeWarpedClock timeWarpedClock;
    private List<IAction> actions = new ArrayList<>();

    public Simulation(double speed)
    {
        this.speed = speed;
        timeWarpedClock = new TimeWarpedClock(speed);
    }

    public Simulation()
    {
        this(1);
    }


    public IClock getClock()
    {
        return timeWarpedClock;
    }

    public Simulation add(IAction action)
    {
        actions.add(action);
        return this;
    }

    public void run(TimeUnit timeUnit, long timeout)
    {
        timeWarpedClock.setStartTime(System.currentTimeMillis());

        actions.forEach(action -> new ThreadBuilder(action,timeWarpedClock).buildAndRun());

        if(!ForkJoinPool.commonPool().awaitQuiescence(timeout, timeUnit))
        {
            throw new RuntimeException("Simulator did not finish in a second."  );
        }

    }
}
