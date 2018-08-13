package com.team2502.ezauton.command;

import com.team2502.ezauton.utils.SimulatedClock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class Simulation
{

    private final SimulatedClock simulatedClock;
    private List<IAction> actions = new ArrayList<>();

    public Simulation()
    {
        simulatedClock = new SimulatedClock();
    }

    public SimulatedClock getSimulatedClock()
    {
        return simulatedClock;
    }

    public Simulation add(IAction action)
    {
        actions.add(action);
        return this;
    }

    /**
     * @param timeoutMillis Max millis
     */
    public void run(long timeoutMillis)
    {
        simulatedClock.init();

        actions.forEach(action -> new ThreadBuilder(action, simulatedClock).buildAndRun());

        simulatedClock.incTimes(timeoutMillis);

        // Need to wait until all threads are finished
        ForkJoinPool.commonPool().awaitQuiescence(1, TimeUnit.SECONDS);
    }

    public void run(TimeUnit timeUnit, long value)
    {
        run(timeUnit.toMillis(value));
    }
}
