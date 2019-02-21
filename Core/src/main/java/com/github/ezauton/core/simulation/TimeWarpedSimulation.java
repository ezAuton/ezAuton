package com.github.ezauton.core.simulation;


import com.github.ezauton.core.action.IAction;
import com.github.ezauton.core.action.tangible.MainActionScheduler;
import com.github.ezauton.core.utils.TimeWarpedClock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A simulator which allows to run in real-time or real-time*{multiplier} 🔥
 */
public class TimeWarpedSimulation implements ISimulation {

    private final double speed;
    private final TimeWarpedClock timeWarpedClock;

    private List<IAction> actions = new ArrayList<>();

    public TimeWarpedSimulation(double speed) {
        this.speed = speed;
        timeWarpedClock = new TimeWarpedClock(speed);
    }

    public TimeWarpedSimulation() {
        this(1);
    }

    public double getSpeed() {
        return speed;
    }

    @Override
    public TimeWarpedClock getClock() {
        return timeWarpedClock;
    }

    public TimeWarpedSimulation add(IAction action) {
        actions.add(action);
        return this;
    }

    /**
     * Run your simulation and blocks until done
     *
     * @param timeout  The amoount of <b>real</b> time that you want your simulation to cap out at.
     * @param timeUnit The timeunit that the timeout is in
     */
    @Override
    public void runSimulation(long timeout, TimeUnit timeUnit) throws TimeoutException, ExecutionException {
        MainActionScheduler mainActionScheduler = new MainActionScheduler(timeWarpedClock);
        List<Future<Void>> futures = new ArrayList<>();
        for (IAction action : actions) {
            final Future<Void> future = mainActionScheduler.scheduleAction(action);
            futures.add(future);
        }
        for (Future<Void> future : futures) {
            try {
                future.get(timeout, timeUnit);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
