package com.github.ezauton.core.action.tangible;

import com.github.ezauton.core.action.Action;
import com.github.ezauton.core.action.ActionRunInfo;
import com.github.ezauton.core.simulation.ActionScheduler;
import com.github.ezauton.core.utils.Clock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class MainActionScheduler implements ActionScheduler {

    private final Clock clock;
    private final boolean print;
    private final List<ActionDescriptor> allFutures = new ArrayList<>();

    public MainActionScheduler(Clock clock, boolean print) {
        this.clock = clock;
        this.print = print;
    }

    public MainActionScheduler(Clock clock) {
        this(clock, false);
    }

    @Override
    public Future<Void> scheduleAction(Action action) {
        final MainActionScheduler pass = new MainActionScheduler(clock, false);
        ActionRunInfo actionRunInfo = new ActionRunInfo(clock, pass);
        final ActionCallable actionCallable = new ActionCallable(action, actionRunInfo, print);
        Future<Void> future = ExecutorPool.getInstance().submit(actionCallable);
        allFutures.add(new ActionDescriptor(action, future));
        return future;
    }

    public void killAll() throws Exception {
        for (ActionDescriptor desc : allFutures) {
            desc.future.cancel(true);
        }
        allFutures.clear();

    }

    private static class ActionDescriptor {
        private final Action action;
        private final Future<Void> future;

        private ActionDescriptor(Action action, Future<Void> future) {
            this.action = action;
            this.future = future;
        }
    }

}
