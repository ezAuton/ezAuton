package org.github.ezauton.ezauton.action.simulation;

import org.github.ezauton.ezauton.action.IAction;
import org.github.ezauton.ezauton.utils.IClock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public interface ISimulation
{
    /**
     * @param action
     * @return self
     */
    ISimulation add(IAction action);

    /**
     * Uses real units for timeout, not simulated
     * @param timeout
     * @param timeUnit
     * @throws TimeoutException
     */
    void runSimulation(long timeout, TimeUnit timeUnit) throws TimeoutException;

    IClock getClock();
}
