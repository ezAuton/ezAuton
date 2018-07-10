package com.team2502.ezauton.command;

import com.team2502.ezauton.localization.Updateable;
import com.team2502.ezauton.utils.ICopyableStopwatch;
import com.team2502.ezauton.utils.SimulatedStopwatch;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SimulatorManager
{

    private static SimulatorManager instance;
    Set<ScheduledAction> scheduledActions = new HashSet<>();
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

    public ICopyableStopwatch generateStopwatch()
    {
        SimulatedStopwatch copy = masterStopwatch.copy();
        copy.reset();
        return copy;
    }

    public void schedule(IAction action, int millisPeriod)
    {
        SimulatedStopwatch stopwatch = new SimulatedStopwatch(millisPeriod * 1E-3);
        scheduledActions.add(new ScheduledAction(action, millisPeriod, stopwatch));
        action.init(stopwatch);
    }

    /**
     * @param timeoutMillis Max millis
     */
    public void run(long timeoutMillis)
    {
        masterStopwatch.reset();
        while(!scheduledActions.isEmpty() && count < timeoutMillis)
        {
            masterStopwatch.progress();
            count++;
            runOnce();
        }
    }

    public void runOnce()
    {
        Iterator<ScheduledAction> iterator = scheduledActions.iterator();
        while(iterator.hasNext())
        {
            ScheduledAction next = iterator.next();
            if(next.count++ % next.period == 0)
            {
                next.simulatedStopwatch.progress();
                IAction action = next.action;
                if(!action.isFinished())
                {
                    action.execute();
                }
                else
                {
                    iterator.remove();
                }
            }
        }
    }

    private static class ScheduledAction implements Updateable
    {

        private final IAction action;
        private final int period;
        private final SimulatedStopwatch simulatedStopwatch;
        private int count = 0;

        ScheduledAction(IAction action, int period, SimulatedStopwatch simulatedStopwatch)
        {
            this.action = action;
            this.period = period;
            this.simulatedStopwatch = simulatedStopwatch;
        }

        @Override
        public boolean update()
        {
            return false;
        }
    }
}