package com.github.ezauton.core.action.tangible;

import com.github.ezauton.core.action.ActionRunInfo;
import com.github.ezauton.core.action.IAction;

import java.util.concurrent.Callable;

public class ActionCallable implements Callable<Void> {

    private final IAction action;
    private final ActionRunInfo actionRunInfo;

    public ActionCallable(IAction action, ActionRunInfo actionRunInfo) {
        this.action = action;
        this.actionRunInfo = actionRunInfo;
    }

    @Override
    public Void call() throws Exception {
        action.run(actionRunInfo);
        action.end();
        action.getFinished().forEach(Runnable::run);
        return null;
    }
}
