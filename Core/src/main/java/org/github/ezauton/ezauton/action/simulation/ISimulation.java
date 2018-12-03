package org.github.ezauton.ezauton.action.simulation;

import org.github.ezauton.ezauton.action.IAction;
import org.github.ezauton.ezauton.utils.IClock;

import java.util.concurrent.TimeUnit;

public interface ISimulation
{
    /**
     * @param action
     * @return self
     */
    ISimulation add(IAction action);

    void run(long timeout, TimeUnit timeUnit);

    IClock getClock();
}
