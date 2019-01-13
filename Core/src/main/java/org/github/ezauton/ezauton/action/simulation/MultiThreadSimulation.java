package org.github.ezauton.ezauton.action.simulation;


import org.github.ezauton.ezauton.action.IAction;
import org.github.ezauton.ezauton.action.ThreadBuilder;
import org.github.ezauton.ezauton.utils.TimeWarpedClock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MultiThreadSimulation implements ISimulation
{

    private final double speed;
    private final TimeWarpedClock timeWarpedClock;

    private List<IAction> actions = new ArrayList<>();

    public MultiThreadSimulation(double speed)
    {
        this.speed = speed;
        timeWarpedClock = new TimeWarpedClock(speed);
    }

    public MultiThreadSimulation()
    {
        this(1);
    }

    public double getSpeed()
    {
        return speed;
    }

    @Override
    public TimeWarpedClock getClock()
    {
        return timeWarpedClock;
    }

    @Override
    public MultiThreadSimulation add(IAction action)
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
        List<Thread> threads = new ArrayList<>();
        for(IAction action : actions)
        {
            threads.add(new ThreadBuilder(action, timeWarpedClock).startAndWait(timeout, timeUnit));
        }
        threads.forEach(Thread::interrupt);
    }


}
