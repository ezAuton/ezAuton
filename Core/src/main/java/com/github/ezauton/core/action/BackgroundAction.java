package com.github.ezauton.core.action;

import java.util.concurrent.TimeUnit;

/**
 * A background is an action that runs forever (until manually ended with {@link IAction#end()}.
 */
public class BackgroundAction extends PeriodicAction {

    /**
     * @param period
     * @param timeUnit
     * @param runnables
     */
    public BackgroundAction(long period, TimeUnit timeUnit, Runnable... runnables) {
        super(period, timeUnit, runnables);
    }

    @Override
    protected boolean isFinished() {
        return false;
    }
}
