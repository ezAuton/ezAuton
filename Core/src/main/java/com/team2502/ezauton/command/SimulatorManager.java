package com.team2502.ezauton.command;

import com.team2502.ezauton.utils.ICopyable;
import com.team2502.ezauton.utils.SimulatedStopwatch;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Describes the singleton class SimulatorManager that manages simulated {@link IAction}s
 */
public class SimulatorManager
{

    private static SimulatorManager instance;
    private Set<ScheduledAction> scheduledActions = new HashSet<>();
    private SimulatedStopwatch masterStopwatch = new SimulatedStopwatch(0.001);
    private long count = 0;

    public static SimulatorManager getInstance()
    {
        if(instance == null)
        {
            instance = new SimulatorManager();
        }

        return instance;
    }

    /**
     * Remove an action from the list of those that are scheduled to run
     *
     * @param baseAction The action to remove
     * @return true if action removed
     */
    public boolean remove(BaseAction baseAction)
    {
        return scheduledActions.removeIf(scheduledAction -> scheduledAction.action == baseAction);
    }

    /**
     * @return A copy of the master stopwatch
     */
    public ICopyable generateStopwatch()
    {
        SimulatedStopwatch copy = masterStopwatch.copy();
        copy.reset();
        return copy;
    }

    /**
     * Schedule an action to be simulated at certain intervals
     *
     * @param action The action to simulate
     * @param millisPeriod
     */
    public void schedule(BaseAction action, long millisPeriod)
    {
        SimulatedStopwatch stopwatch = new SimulatedStopwatch(millisPeriod * 1E-3);
        stopwatch.reset();
        scheduledActions.add(new ScheduledAction(action, millisPeriod, stopwatch));
        action.init(stopwatch);
    }

    /**
     * @param timeoutMillis Max millis
     */
    public void run(long timeoutMillis)
    {
        count = 0;
        masterStopwatch.reset();
        while(!scheduledActions.isEmpty())
        {
            if(count > timeoutMillis)
            {
                throw new RuntimeException("Hit timeout!");
            }
            masterStopwatch.progress();
            count++;
            runOnce();
        }
    }

    public void runOnce()
    {
        Iterator<ScheduledAction> iterator = scheduledActions.iterator();
        Set<BaseAction> finished = new HashSet<>();
        while(iterator.hasNext())
        {
            ScheduledAction next = iterator.next();
            if(next.count++ % next.period == 0)
            {
                next.simulatedStopwatch.progress();
                BaseAction action = next.action;
                if(!action.isFinished())
                {
                    action.execute();
                }
                else
                {
                    finished.add(action);
                    iterator.remove();
                }
            }
        }
        finished.forEach(action -> action.getRunnables().forEach(Runnable::run));
    }

    public long getCount()
    {
        return count;
    }

    private static class ScheduledAction
    {

        private final BaseAction action;
        private final long period;
        private final SimulatedStopwatch simulatedStopwatch;
        private int count = 0;

        ScheduledAction(BaseAction action, long period, SimulatedStopwatch simulatedStopwatch)
        {
            this.action = action;
            this.period = period;
            this.simulatedStopwatch = simulatedStopwatch;
        }
    }
}
