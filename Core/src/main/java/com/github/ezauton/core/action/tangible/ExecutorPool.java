package com.github.ezauton.core.action.tangible;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorPool {

    private static ExecutorService executorService = null;

    public static ExecutorService getInstance() {
        if (executorService == null) executorService = Executors.newCachedThreadPool();
        return executorService;
    }
}
