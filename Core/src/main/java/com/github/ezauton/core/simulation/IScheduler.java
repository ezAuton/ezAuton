package com.github.ezauton.core.simulation;

import com.github.ezauton.core.action.IAction;

/**
 * An interface which is used to schedule an action in a certain way. Nice for simulations.
 */
public interface IScheduler {
    /**
     * @param action
     * @return the action
     */
    void scheduleAction(IAction action);
}
