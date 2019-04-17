package com.github.ezauton.core.action.tangible;

import com.github.ezauton.core.action.ActionRunInfo;
import com.github.ezauton.core.action.Action;
import com.github.ezauton.core.simulation.ActionScheduler;
import com.github.ezauton.core.utils.Clock;

import java.util.concurrent.Future;

public class MainActionScheduler implements ActionScheduler {

    private final Clock clock;
    private final boolean print;

    public MainActionScheduler(Clock clock, boolean print) {
        this.clock = clock;
        this.print = print;
    }

    public MainActionScheduler(Clock clock)
    {
        this(clock, false);
    }

    @Override
    public Future<Void> scheduleAction(Action action) {
        final MainActionScheduler pass = new MainActionScheduler(clock, false);
        ActionRunInfo actionRunInfo = new ActionRunInfo(clock,  pass);
        final ActionCallable actionCallable = new ActionCallable(action, actionRunInfo, print);
        return ExecutorPool.getInstance().submit(actionCallable);
    }

}
