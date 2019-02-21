package com.github.ezauton.core.action.tangible;

import com.github.ezauton.core.action.IAction;
import com.github.ezauton.core.utils.IClock;

import java.util.concurrent.Callable;

public class ActionCallable implements Callable<Void> {

    private final IAction action;
    private final IClock clock;

    public ActionCallable(IAction action, IClock clock){
        this.action = action;
        this.clock = clock;
    }

    @Override
    public Void call() throws Exception {
        action.run(clock);
        action.end();
        action.getFinished().forEach(Runnable::run);
        return null;
    }
}
