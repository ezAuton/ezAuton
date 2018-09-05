package com.team2502.ezauton.test.utils;

import com.team2502.ezauton.command.*;
import com.team2502.ezauton.utils.RealClock;
import com.team2502.ezauton.utils.Stopwatch;
import com.team2502.ezauton.utils.TimeWarpedClock;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class ActionTest
{

    @Test
    public void testDelayedAction()
    {
        Simulation sim = new Simulation(10);

        int delay = 3;
        DelayedAction action = new DelayedAction(TimeUnit.SECONDS, delay); // w
        action.onFinish(() -> System.out.println("[testDelayedAction] The delayed action finished"));

        sim.add(action);

        Stopwatch stopwatch = new Stopwatch(RealClock.CLOCK);

        stopwatch.resetIfNotInit();
        sim.run(TimeUnit.SECONDS, 2);
        assertEquals(delay, stopwatch.pop(TimeUnit.SECONDS) * 10, 0.2);
    }

    @Test
    public void testActionGroupSingleNoSim()
    {
        TimeWarpedClock clock = new TimeWarpedClock(10);

        AtomicInteger count = new AtomicInteger(0);
        count.compareAndSet(0, 1);
        assertEquals(1, count.get());

        DelayedAction action = new DelayedAction(TimeUnit.SECONDS, 3, () -> count.compareAndSet(1, 3));
        action.onFinish(() -> count.compareAndSet(3, 4));
        ActionGroup group = new ActionGroup()
                .addSequential(action);

        clock.setStartTime(System.currentTimeMillis());
        group.run(clock);
        assertEquals(4, count.get());
    }

    @Test
    public void testActionGroupSingle()
    {
        AtomicInteger count = new AtomicInteger(0);
        count.compareAndSet(0, 1);
        assertEquals(1, count.get());
        DelayedAction action = new DelayedAction(TimeUnit.SECONDS, 3);
        action.onFinish(() -> count.compareAndSet(1, 4));
        ActionGroup group = new ActionGroup()
                .addSequential(action);

        Simulation sim = new Simulation(10);
        sim.add(group);
        sim.run(TimeUnit.SECONDS, 3);
        assertEquals(4, count.get());

    }

    @Test
    public void testActionGroupSequentialThreadBuilder() throws InterruptedException
    {
        AtomicInteger count = new AtomicInteger(0);

        DelayedAction two = new DelayedAction(TimeUnit.MILLISECONDS, 200);
        two.onFinish(() -> {
            System.out.println("Two finished");
            count.compareAndSet(0, 2);
        });

        DelayedAction three = new DelayedAction(TimeUnit.MILLISECONDS, 300);
        two.onFinish(() -> {
            System.out.println("three finished");
            count.compareAndSet(2, 3);
        });


        ActionGroup group = new ActionGroup()
                .addSequential(two)
                .addSequential(three)
                .onFinish(() -> {
                    System.out.println("all done");

                    count.compareAndSet(3, 4);

                });

        Thread thread = new ThreadBuilder(group).build();
        thread.start();
        thread.join(1000);
        assertEquals(4, count.get());
    }

    @Test
    public void testActionGroup()
    {
        AtomicInteger count = new AtomicInteger(0);

        DelayedAction five = new DelayedAction(TimeUnit.SECONDS, 5); //then
        five.onFinish(() -> count.compareAndSet(3, 5));
        five.onFinish(() -> System.out.println("five finished"));

        DelayedAction three1 = new DelayedAction(TimeUnit.SECONDS, 3); // first
        three1.onFinish(() -> count.compareAndSet(0, 3));
        three1.onFinish(() -> System.out.println("three1 finished"));

        DelayedAction three2 = new DelayedAction(TimeUnit.SECONDS, 3); // last
        three2.onFinish(() -> count.compareAndSet(5, 8));
        three2.onFinish(() -> System.out.println("three2 finished"));

        // three1 and five run in parallel
        // when five has 2 seconds left, three2 starts
        // then finally actiongroup should terminate

        ActionGroup actionGroup = new ActionGroup()
                .addParallel(three1)
                .addSequential(five)
                .addSequential(three2)
                .onFinish(() -> {
                    System.out.println("done");
                    count.compareAndSet(8, 10);
                });

        Simulation simulation = new Simulation(10);
        simulation.add(actionGroup);
        simulation.run(TimeUnit.SECONDS, 100);

        assertEquals(10, count.intValue());
    }

    @Test
    public void testActionGroupSequential()
    {
        AtomicInteger count = new AtomicInteger(0);

        DelayedAction two = new DelayedAction(TimeUnit.SECONDS, 2);
        two.onFinish(() -> {
            System.out.println("Two finished");
            count.compareAndSet(0, 2);
        });

        DelayedAction three = new DelayedAction(TimeUnit.SECONDS, 3);
        two.onFinish(() -> {
            System.out.println("three finished");
            count.compareAndSet(2, 3);
        });


        ActionGroup group = new ActionGroup()
                .addSequential(two)
                .addSequential(three)
                .onFinish(() -> {
                    System.out.println("all done");

                    count.compareAndSet(3, 4);

                });

        Simulation simulation = new Simulation();
        simulation.add(group);
        simulation.run(TimeUnit.SECONDS, 100);

        assertEquals(4, count.get());
    }

    @Test
    public void testWithActionGroup()
    {
        AtomicInteger counter = new AtomicInteger(0);
        BackgroundAction actionA = new BackgroundAction(TimeUnit.MILLISECONDS, 20, () -> {
            counter.incrementAndGet();
            return true;
        });

        int expectedValue = 40;
        DelayedAction mainAction = new DelayedAction(TimeUnit.MILLISECONDS, 20 * expectedValue);

        ActionGroup group = new ActionGroup()
                .with(actionA)
                .addSequential(mainAction);

        Simulation sim = new Simulation(10);
        sim.add(group);

        sim.run(TimeUnit.SECONDS, 10);
        System.out.println("counter = " + counter.get());
        assertEquals(expectedValue, counter.get(), expectedValue * (4F/5F));
    }
}
