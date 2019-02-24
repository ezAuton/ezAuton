package com.github.ezauton.core.action;

import com.github.ezauton.core.simulation.ActionScheduler;
import com.github.ezauton.core.utils.Clock;

public class ActionRunInfo {

    private final Clock clock;
    private final ActionScheduler actionScheduler;

    public ActionRunInfo(Clock clock, ActionScheduler actionScheduler) {
        this.clock = clock;
        this.actionScheduler = actionScheduler;
    }

    public Clock getClock() {
        return clock;
    }

    public ActionScheduler getActionScheduler() {
        return actionScheduler;
    }
}
