package com.github.ezauton.core.action.tangible;

import com.github.ezauton.core.action.Action;
import com.github.ezauton.core.action.ActionRunInfo;

import java.util.concurrent.Callable;

public class ActionCallable implements Callable<Void> {

    private final Action action;
    private final ActionRunInfo actionRunInfo;

    public ActionCallable(Action action, ActionRunInfo actionRunInfo) {
        this.action = action;
        this.actionRunInfo = actionRunInfo;
    }

    @Override
    public Void call() throws Exception {
        try {
            action.run(actionRunInfo);
        }catch (Exception e){
            action.interrupted();
            throw e;
        }
        action.end(); // end is normally called if no exception has yet been called
        action.getFinished().forEach(Runnable::run);
        return null;
    }
}
