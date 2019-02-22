package com.github.ezauton.core.simulation;

import com.github.ezauton.core.utils.IClock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A simulator for actions
 */
public interface ISimulation extends IScheduler {

    /**
     * Uses real units for timeout, not simulated
     *
     * @param timeout
     * @param timeUnit
     * @throws TimeoutException
     */
    void runSimulation(long timeout, TimeUnit timeUnit) throws TimeoutException;

    IClock getClock();
}
