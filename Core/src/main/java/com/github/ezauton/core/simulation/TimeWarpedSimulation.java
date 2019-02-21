package com.github.ezauton.core.simulation;


import com.github.ezauton.core.action.IAction;
import com.github.ezauton.core.action.tangible.ProcessBuilder;
import com.github.ezauton.core.utils.TimeWarpedClock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
     * Run your simulation
     *
     * @param timeout  The amoount of <b>real</b> time that you want your simulation to cap out at.
     * @param timeUnit The timeunit that the timeout is in
     */
    public void runSimulation(long timeout, TimeUnit timeUnit) {
        List<Thread> threads = new ArrayList<>();
        for (IAction action : actions) {
            threads.add(new ProcessBuilder(action, timeWarpedClock).startAndWait(timeout, timeUnit));
        }
        threads.forEach(Thread::interrupt);
    }


    @Override
    public void scheduleAction(IAction action) {
        add(action);
    }
}
