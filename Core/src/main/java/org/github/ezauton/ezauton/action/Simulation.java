package org.github.ezauton.ezauton.action;


import org.github.ezauton.ezauton.utils.TimeWarpedClock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Simulation
{

    private final double speed;
    private final TimeWarpedClock timeWarpedClock;

    private List<IAction> actions = new ArrayList<>();

    public Simulation(double speed)
    {
        this.speed = speed;
        timeWarpedClock = new TimeWarpedClock(speed);
    }

    public Simulation()
    {
        this(1);
    }

    public double getSpeed()
    {
        return speed;
    }

    public TimeWarpedClock getClock()
    {
        return timeWarpedClock;
    }

    public Simulation add(IAction action)
    {
        actions.add(action);
        return this;
    }

    /**
     * Run your simulation
     *
     * @param timeout  The amoount of <b>real</b> time that you want your simulation to cap out at.
     * @param timeUnit The timeunit that the timeout is in
     */
    public void run(long timeout, TimeUnit timeUnit)
    {
        timeWarpedClock.setStartTime(System.currentTimeMillis());

        actions.forEach(action -> new ThreadBuilder(action, timeWarpedClock).startAndWait(timeout, timeUnit));
    }
}
