package com.github.ezauton.core.action.tangible;

import com.github.ezauton.core.action.ActionRunInfo;
import com.github.ezauton.core.action.IAction;
import com.github.ezauton.core.simulation.ActionScheduler;
import com.github.ezauton.core.utils.IClock;

import java.util.concurrent.Future;

public class MainActionScheduler implements ActionScheduler {

    private final IClock clock;

    public MainActionScheduler(IClock clock) {
        this.clock = clock;
    }

    @Override
    public Future<Void> scheduleAction(IAction action) {
        ActionRunInfo actionRunInfo = new ActionRunInfo(clock, this);
        final ActionCallable actionCallable = new ActionCallable(action, actionRunInfo);
        return ExecutorPool.getInstance().submit(actionCallable);
    }
}
