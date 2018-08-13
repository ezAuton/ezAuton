package com.team2502.ezauton.test.utils;

import com.team2502.ezauton.command.ActionGroup;
import com.team2502.ezauton.command.DelayedAction;
import com.team2502.ezauton.command.Simulation;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class ActionTest
{

    @Test
    public void testActionGroup()
    {
        AtomicInteger count = new AtomicInteger(0);

        DelayedAction five = new DelayedAction(TimeUnit.SECONDS, 5);
        five.onFinish(() -> count.compareAndSet(5, 7));

        DelayedAction three1 = new DelayedAction(TimeUnit.SECONDS, 3);
        three1.onFinish(() -> count.compareAndSet(0, 3));

        DelayedAction three2 = new DelayedAction(TimeUnit.SECONDS, 3);
        three2.onFinish(() -> count.compareAndSet(7, 8));

        ActionGroup actionGroup = new ActionGroup()
                .addParallel(five)
                .addSequential(three1)
                .addParallel(three2)
                .onFinish(() -> count.compareAndSet(3, 5));

        Simulation simulation = new Simulation();
        simulation.add(actionGroup);
        simulation.run(TimeUnit.SECONDS, 100);

        assertEquals(8, count.intValue());
    }
}
