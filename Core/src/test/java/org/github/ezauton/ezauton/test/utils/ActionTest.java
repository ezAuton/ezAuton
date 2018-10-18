package org.github.ezauton.ezauton.test.utils;

import org.github.ezauton.ezauton.action.*;
import org.github.ezauton.ezauton.action.simulation.MultiThreadSimulation;
import org.github.ezauton.ezauton.utils.RealClock;
import org.github.ezauton.ezauton.utils.Stopwatch;
import org.github.ezauton.ezauton.utils.TimeWarpedClock;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class ActionTest
{

    @Test
    public void testDelayedAction()
    {
        MultiThreadSimulation sim = new MultiThreadSimulation(10);

        int delay = 3;
        DelayedAction action = new DelayedAction(delay, TimeUnit.SECONDS); // w
        action.onFinish(() -> System.out.println("[testDelayedAction] The delayed action finished"));

        sim.add(action);

        Stopwatch stopwatch = new Stopwatch(RealClock.CLOCK);

        stopwatch.resetIfNotInit();
        sim.run(2, TimeUnit.SECONDS);
        assertEquals(delay, stopwatch.pop(TimeUnit.SECONDS) * 10, 0.2);
    }

    @Test
    public void testActionGroupSingleNoSim()
    {
        TimeWarpedClock clock = new TimeWarpedClock(10);

        AtomicInteger count = new AtomicInteger(0);
        count.compareAndSet(0, 1);
        assertEquals(1, count.get());

        DelayedAction action = new DelayedAction(3, TimeUnit.SECONDS, () -> count.compareAndSet(1, 3));
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
        DelayedAction action = new DelayedAction(3, TimeUnit.SECONDS);
        action.onFinish(() -> count.compareAndSet(1, 4));
        ActionGroup group = new ActionGroup()
                .addSequential(action);

        MultiThreadSimulation sim = new MultiThreadSimulation(10);
        sim.add(group);
        sim.run(3, TimeUnit.SECONDS);
        assertEquals(4, count.get());

    }

    @Test
    public void testActionGroupSequentialThreadBuilder() throws InterruptedException
    {
        AtomicInteger count = new AtomicInteger(0);

        DelayedAction two = new DelayedAction(200, TimeUnit.MILLISECONDS);
        two.onFinish(() -> {
            System.out.println("Two finished");
            count.compareAndSet(0, 2);
        });

        DelayedAction three = new DelayedAction(300, TimeUnit.MILLISECONDS);
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

        new ThreadBuilder(group).startAndWait(1, TimeUnit.SECONDS);
        assertEquals(4, count.get());
    }

    @Test
    public void testActionGroup()
    {
        AtomicInteger count = new AtomicInteger(0);

        DelayedAction five = new DelayedAction(5, TimeUnit.SECONDS); //then
        five.onFinish(() -> count.compareAndSet(3, 5));
        five.onFinish(() -> System.out.println("five finished"));

        DelayedAction three1 = new DelayedAction(3, TimeUnit.SECONDS); // first
        three1.onFinish(() -> count.compareAndSet(0, 3));
        three1.onFinish(() -> System.out.println("three1 finished"));

        DelayedAction three2 = new DelayedAction(3, TimeUnit.SECONDS); // last
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

        MultiThreadSimulation simulation = new MultiThreadSimulation(10);
        simulation.add(actionGroup);
        simulation.run(100, TimeUnit.SECONDS);

        assertEquals(10, count.intValue());
    }

    @Test
    public void testActionGroupSequential()
    {
        AtomicInteger count = new AtomicInteger(0);

        DelayedAction two = new DelayedAction(2, TimeUnit.SECONDS);
        two.onFinish(() -> {
            System.out.println("Two finished");
            count.compareAndSet(0, 2);
        });

        DelayedAction three = new DelayedAction(3, TimeUnit.SECONDS);
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

        MultiThreadSimulation simulation = new MultiThreadSimulation();
        simulation.add(group);
        simulation.run(100, TimeUnit.SECONDS);

        assertEquals(4, count.get());
    }

    @Test
    public void testWithActionGroup()
    {
        AtomicInteger counter = new AtomicInteger(0);

        BackgroundAction actionA = new BackgroundAction(20, TimeUnit.MILLISECONDS);

        actionA.addUpdateable(() -> {
            counter.incrementAndGet();
            return true;
        });

        actionA.setPeriodDelayAfterExecution(false);

        int expectedValue = 40;

        DelayedAction mainAction = new DelayedAction(20 * expectedValue, TimeUnit.MILLISECONDS);

        ActionGroup group = new ActionGroup()
                .with(actionA)
                .addSequential(mainAction);

        MultiThreadSimulation sim = new MultiThreadSimulation(1);

        sim.add(group);

        sim.run(10, TimeUnit.SECONDS);
        System.out.println("counter = " + counter.get());
        assertEquals(expectedValue, counter.get(), expectedValue * (19F / 20F));
    }

    @Test // TODO: add
    public void testTimedAction()
    {
//        AtomicLong count = new AtomicLong(0);
//
//        long expectedDTms = 3000;
//        TimedPeriodicAction action = new TimedPeriodicAction(TimeUnit.MILLISECONDS, expectedDTms, () -> count.set(System.currentTimeMillis()));
//
//        Simulation sim = new Simulation(1);
//        sim.add(action);
//
//        long initmillis = System.currentTimeMillis();
//        sim.run(TimeUnit.SECONDS, 12);
//        System.out.println("count.get() - initmillis = " + (count.get() - initmillis));
//        assertEquals(expectedDTms, count.get() - initmillis, 50);


    }

}
