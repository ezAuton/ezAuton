package com.team2502.ezauton.command;

import com.team2502.ezauton.utils.IStopwatch;
import com.team2502.ezauton.utils.RealStopwatch;
import com.team2502.ezauton.utils.SimulatedStopwatch;
import edu.wpi.first.wpilibj.command.Command;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public interface IAction
{
    default void init(IStopwatch stopwatch)
    {

    }

    default void execute()
    {

    }

    boolean isFinished();

    /**
     * Combines two actions together
     *
     * @param other
     * @param bothCompleteForFinish
     * @return
     */
    default IAction addSubAction(IAction other, boolean bothCompleteForFinish)
    {
        return new IAction()
        {
            @Override
            public void init(IStopwatch stopwatch)
            {
                IAction.this.init(stopwatch);
                other.init(stopwatch);
            }

            @Override
            public void execute()
            {
                IAction.this.execute();
                other.execute();
            }

            @Override
            public boolean isFinished()
            {
                if(bothCompleteForFinish)
                {
                    return IAction.this.isFinished() && other.isFinished();
                }
                else
                {
                    return IAction.this.isFinished();
                }
            }
        };
    }

    /**
     * @return A WPILib command
     */
    default Command buildWPI()
    {
        return new CommandCreator(this);
    }

    /**
     * @return A Thread which acts like a command but can be run faster
     */
    default Thread buildThread(long millisPeriod)
    {
        return new Thread(() -> {
            RealStopwatch stopwatch = new RealStopwatch();
            init(stopwatch);
            while(!isFinished())
            {
                execute();
                try
                {
                    Thread.sleep(millisPeriod);
                }
                catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Test command (with optional other commands). If there are multiple commands, all the other
     * executes of the other commands will be run before moving on to the next iteration of calling
     * execute on this command and the rest of the commands. This is instantaneous and will generally
     * be used for simulation.
     *
     * @param with
     * @deprecated Use {@link SimulatorManager}
     */
    default void testWith(SimulatedStopwatch mainStopwatch, IAction... with)
    {
        init(mainStopwatch);
        Map<IAction, SimulatedStopwatch> actionMap = new HashMap<>();

        for(IAction action : with)
        {
            SimulatedStopwatch stopwatch = mainStopwatch.copy();
            actionMap.put(action, stopwatch);
            action.init(stopwatch);
        }

        boolean allFinished = false;
        while(!allFinished)
        {
            boolean notFinished = false;
            if(!isFinished())
            {
                execute();
                notFinished = true;
            }
            Iterator<Map.Entry<IAction, SimulatedStopwatch>> iterator = actionMap.entrySet().iterator();
            while(iterator.hasNext())
            {
                Map.Entry<IAction, SimulatedStopwatch> entry = iterator.next();
                IAction action = entry.getKey();
                if(!action.isFinished())
                {
                    notFinished = true;
                }
                else
                {
                    iterator.remove();
                }
            }
            if(!notFinished)
            {
                allFinished = true;
            }
            mainStopwatch.progress();
        }
    }
}
