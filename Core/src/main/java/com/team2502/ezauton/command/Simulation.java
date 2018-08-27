package com.team2502.ezauton.command;

import com.team2502.ezauton.utils.FastClock;
import com.team2502.ezauton.utils.IClock;
import com.team2502.ezauton.utils.SimulatedClock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class Simulation
{

    private final IClock simulatedClock;
    private final static double scaleFactor = 10; // Simulations will run 10x as fast as real life
    private List<IAction> actions = new ArrayList<>();

    public Simulation()
    {
        simulatedClock = new FastClock(scaleFactor);
    }

    public IClock getSimulatedClock()
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
        actions.forEach(action -> new ThreadBuilder(action, simulatedClock).buildAndRun());

        // Need to wait until all threads are finished
        if(!ForkJoinPool.commonPool().awaitQuiescence(1, TimeUnit.SECONDS))
        {
            throw new RuntimeException("Simulator did not finish in a second."  );
        }

    }

    public void run(TimeUnit timeUnit, long value)
    {
        run(timeUnit.toMillis(value));
    }
}
