package com.github.ezauton.core.action.tangible;

import com.github.ezauton.core.action.ActionRunInfo;
import com.github.ezauton.core.action.Action;
import com.github.ezauton.core.simulation.ActionScheduler;
import com.github.ezauton.core.utils.Clock;

import java.util.concurrent.Future;

public class MainActionScheduler implements ActionScheduler {

    private final Clock clock;

    public MainActionScheduler(Clock clock) {
        this.clock = clock;
    }

    @Override
    public Future<Void> scheduleAction(Action action) {
        ActionRunInfo actionRunInfo = new ActionRunInfo(clock, this);
        final ActionCallable actionCallable = new ActionCallable(action, actionRunInfo);
        return ExecutorPool.getInstance().submit(actionCallable);
    }
}
