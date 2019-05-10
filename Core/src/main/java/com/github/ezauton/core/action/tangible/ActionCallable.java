package com.github.ezauton.core.action.tangible;

import com.github.ezauton.core.action.Action;
import com.github.ezauton.core.action.ActionRunInfo;

import java.util.concurrent.Callable;

public class ActionCallable implements Callable<Void> {

    private final Action action;
    private final ActionRunInfo actionRunInfo;
    private final boolean print;

    public ActionCallable(Action action, ActionRunInfo actionRunInfo) {
        this(action, actionRunInfo, false);
    }

    public ActionCallable(Action action, ActionRunInfo actionRunInfo, boolean print){
        this.action = action;
        this.actionRunInfo = actionRunInfo;
        this.print = print;
    }

    @Override
    public Void call() throws Exception {
        try {
            action.run(actionRunInfo);
        }catch (Exception e){
            if (print) e.printStackTrace();
            throw e;
        }
        action.end(); // end is normally called if no exception has yet been called
        action.getFinished().forEach(Runnable::run);
        return null;
    }
}
