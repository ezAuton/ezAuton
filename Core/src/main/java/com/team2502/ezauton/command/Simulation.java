package com.team2502.ezauton.command;

import com.team2502.ezauton.utils.SimulatedClock;

import java.util.ArrayList;
import java.util.List;

public class Simulation
{
    private List<IAction> actions = new ArrayList<>();

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
        SimulatedClock simulatedClock = new SimulatedClock();

        simulatedClock.init();

        actions.forEach(action -> action.run(simulatedClock));

        simulatedClock.incTimes(timeoutMillis);
    }
}
