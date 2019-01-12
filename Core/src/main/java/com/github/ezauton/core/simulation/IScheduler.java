package com.github.ezauton.core.simulation;

import com.github.ezauton.core.action.IAction;

public interface IScheduler {
    /**
     * @param action
     * @return the action
     */
    void scheduleAction(IAction action);
}
