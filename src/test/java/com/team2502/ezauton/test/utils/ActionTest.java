package com.team2502.ezauton.test.utils;

import com.team2502.ezauton.command.ActionGroup;
import com.team2502.ezauton.command.BaseAction;
import com.team2502.ezauton.command.SimulatorManager;
import com.team2502.ezauton.command.TimedAction;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class ActionTest
{
    @Test
    public void testTimedAction()
    {
        AtomicInteger integer = new AtomicInteger(0);
        BaseAction baseAction = new TimedAction(3)
        {
            @Override
            protected void execute()
            {
                integer.incrementAndGet();
            }
        };

        Thread thread = baseAction.buildThread(1000);
        thread.start();
        try
        {
            thread.join();
            assertEquals(3, integer.intValue());
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void testActionGroup()
    {
        AtomicInteger count = new AtomicInteger(0);

        TimedAction timedAction1 = new TimedAction(5);
        timedAction1.onFinish(() -> count.compareAndSet(3, 4));

        TimedAction timedAction2 = new TimedAction(3);
        timedAction2.onFinish(() -> count.addAndGet(3));

        TimedAction timedAction3 = new TimedAction(3);
        timedAction3.onFinish(() -> count.addAndGet(3));

        ActionGroup actionGroup = new ActionGroup()
                .addParallel(timedAction1)
                .addSequential(timedAction2)
                .addParallel(timedAction3);

        actionGroup.simulate(20);
        SimulatorManager.getInstance().run(100_000);
        assertEquals(7, count.intValue());
    }

    @Test
    public void testWith()
    {

        AtomicInteger atomicInteger = new AtomicInteger(0);
        BaseAction five = new TimedAction(5);
        BaseAction ten = new TimedAction(10);
        BaseAction seven = new TimedAction(7).onFinish(() -> {
            if(!SimulatorManager.getInstance().remove(ten))
            {
                atomicInteger.compareAndSet(0, 2);
            }
        });

        ActionGroup actionGroup = new ActionGroup()
                .with(ten)
                .addParallel(seven)
                .addSequential(five);

        actionGroup.simulate(20);
        SimulatorManager.getInstance().run(100_000);
        assertEquals(2, atomicInteger.intValue());
    }
}
