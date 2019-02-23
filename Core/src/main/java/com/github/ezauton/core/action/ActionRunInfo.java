package com.github.ezauton.core.action;

import com.github.ezauton.core.simulation.ActionScheduler;
import com.github.ezauton.core.utils.IClock;

public class ActionRunInfo {

    private final IClock clock;
    private final ActionScheduler actionScheduler;

    public ActionRunInfo(IClock clock, ActionScheduler actionScheduler) {
        this.clock = clock;
        this.actionScheduler = actionScheduler;
    }

    public IClock getClock() {
        return clock;
    }

    public ActionScheduler getActionScheduler() {
        return actionScheduler;
    }
}
