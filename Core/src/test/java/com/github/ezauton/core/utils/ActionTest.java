package com.github.ezauton.core.utils;

import com.github.ezauton.core.action.*;
import com.github.ezauton.core.action.tangible.MainActionScheduler;
import com.github.ezauton.core.localization.Updateable;
import com.github.ezauton.core.localization.UpdateableGroup;
import com.github.ezauton.core.simulation.ActionScheduler;
import com.github.ezauton.core.simulation.TimeWarpedSimulation;
import com.github.ezauton.core.utils.RealClock;
import com.github.ezauton.core.utils.Stopwatch;
import com.github.ezauton.core.utils.TimeWarpedClock;
import com.google.common.util.concurrent.AtomicDouble;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;


public class ActionTest {

    private ActionScheduler actionScheduler = new MainActionScheduler(RealClock.CLOCK);

    @Test
    public void testScheduleActionInterface() throws InterruptedException, TimeoutException, ExecutionException {
        AtomicLong atomicLong = new AtomicLong(0);
        Action action = new Action() {

            @Override
            public void run(ActionRunInfo actionRunInfo) {
                atomicLong.set(actionRunInfo.getClock().getTime());
            }

            @Override
            public void end() {
                // Not implemented
            }

            @Override
            public Action onFinish(Runnable onFinish) {
                return this; // Not implemented
            }

            @Override
            public List<Runnable> getFinished() {
                return Collections.emptyList(); // Not implemented
            }
        };

        actionScheduler.scheduleAction(action).get(1_000, TimeUnit.MILLISECONDS);

        assertEquals(System.currentTimeMillis(), atomicLong.get(), 1_000);
    }

    @Test
    public void testDelayedActionInterrupt() {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);

        DelayedAction delayedAction = new DelayedAction(20, TimeUnit.SECONDS, () -> atomicBoolean.set(true));
        final Future<Void> voidFuture = actionScheduler.scheduleAction(delayedAction);
        voidFuture.cancel(true);
        assertThrows(CancellationException.class, () -> voidFuture.get(1_000, TimeUnit.MILLISECONDS));
        assertFalse(atomicBoolean.get());
    }

    @Test
    public void testDelayedAction() throws TimeoutException, ExecutionException {
        TimeWarpedSimulation sim = new TimeWarpedSimulation(10);

        int delay = 3;
        DelayedAction action = new DelayedAction(delay, TimeUnit.SECONDS); // w
//        action.onFinish(() -> System.out.println("[testDelayedAction] The delayed action finished"));

        sim.add(action);

        Stopwatch stopwatch = new Stopwatch(RealClock.CLOCK);

        stopwatch.resetIfNotInit();
        sim.runSimulation(2, TimeUnit.SECONDS);
        assertEquals(delay, stopwatch.pop(TimeUnit.SECONDS) * 10, 0.2);
    }

    @Test
    public void testActionGroupSingleNoSim() throws ExecutionException, TimeoutException, InterruptedException {

        TimeWarpedClock clock = new TimeWarpedClock(10);

        ActionScheduler actionScheduler = new MainActionScheduler(clock);

        AtomicInteger count = new AtomicInteger(0);
        count.compareAndSet(0, 1);
        assertEquals(1, count.get());

        DelayedAction action = new DelayedAction(3, TimeUnit.SECONDS, () -> count.compareAndSet(1, 3));
        action.onFinish(() -> count.compareAndSet(3, 4));
        ActionGroup group = new ActionGroup()
                .addSequential(action);


        final Future<Void> voidFuture = actionScheduler.scheduleAction(group);
        voidFuture.get(10, TimeUnit.SECONDS);
        assertEquals(4, count.get());
    }

    @Test
    public void testActionGroupSingle() throws TimeoutException, ExecutionException {
        AtomicInteger count = new AtomicInteger(0);
        count.compareAndSet(0, 1);
        assertEquals(1, count.get());
        DelayedAction action = new DelayedAction(3, TimeUnit.SECONDS);
        action.onFinish(() -> count.compareAndSet(1, 4));
        ActionGroup group = new ActionGroup()
                .addSequential(action);

        TimeWarpedSimulation sim = new TimeWarpedSimulation(10);
        sim.add(group);
        sim.runSimulation(3, TimeUnit.SECONDS);
        assertEquals(4, count.get());

    }

    @Test
    public void testActionGroupSequentialThreadBuilder() throws InterruptedException, ExecutionException, TimeoutException {
        AtomicInteger count = new AtomicInteger(0);

        DelayedAction two = new DelayedAction(200, TimeUnit.MILLISECONDS);
        two.onFinish(() -> {
//            System.out.println("Two finished");
            count.compareAndSet(0, 2);
        });

        DelayedAction three = new DelayedAction(300, TimeUnit.MILLISECONDS);
        two.onFinish(() -> {
//            System.out.println("three finished");
            count.compareAndSet(2, 3);
        });


        Action group = new ActionGroup()
                .addSequential(two)
                .addSequential(three)
                .onFinish(() -> {
//                    System.out.println("all done");

                    count.compareAndSet(3, 4);

                });

//        new ProcessBuilder(group).startAndWait(1, TimeUnit.SECONDS);
        actionScheduler.scheduleAction(group).get(1, TimeUnit.SECONDS);
        assertEquals(4, count.get());
    }

    @Test
    public void testActionGroup() throws TimeoutException, ExecutionException {
        AtomicInteger count = new AtomicInteger(0);

        DelayedAction five = new DelayedAction(5, TimeUnit.SECONDS); //then
        five.onFinish(() -> count.compareAndSet(3, 5));
//        five.onFinish(() -> System.out.println("five finished"));

        DelayedAction three1 = new DelayedAction(3, TimeUnit.SECONDS); // first
        three1.onFinish(() -> count.compareAndSet(0, 3));
//        three1.onFinish(() -> System.out.println("three1 finished"));

        DelayedAction three2 = new DelayedAction(3, TimeUnit.SECONDS); // last
        three2.onFinish(() -> count.compareAndSet(5, 8));
//        three2.onFinish(() -> System.out.println("three2 finished"));

        // three1 and five run in parallel
        // when five has 2 seconds left, three2 starts
        // then finally actiongroup should terminate

        Action actionGroup = new ActionGroup()
                .addParallel(three1)
                .addSequential(five)
                .addSequential(three2)
                .onFinish(() -> {
//                    System.out.println("done");
                    count.compareAndSet(8, 10);
                });

        TimeWarpedSimulation simulation = new TimeWarpedSimulation(10);
        simulation.add(actionGroup);
        simulation.runSimulation(100, TimeUnit.SECONDS);

        assertEquals(10, count.intValue());
    }

    @Test
    public void testActionGroupSequential() throws TimeoutException, ExecutionException {
        AtomicInteger count = new AtomicInteger(0);

        DelayedAction two = new DelayedAction(2, TimeUnit.SECONDS);
        two.onFinish(() -> {
//            System.out.println("Two finished");
            count.compareAndSet(0, 2);
        });

        DelayedAction three = new DelayedAction(3, TimeUnit.SECONDS);
        two.onFinish(() -> {
//            System.out.println("three finished");
            count.compareAndSet(2, 3);
        });


        Action group = new ActionGroup()
                .addSequential(two)
                .addSequential(three)
                .onFinish(() -> {
//                    System.out.println("all done");

                    count.compareAndSet(3, 4);

                });

        TimeWarpedSimulation simulation = new TimeWarpedSimulation();
        simulation.add(group);
        simulation.runSimulation(100, TimeUnit.SECONDS);

        assertEquals(4, count.get());
    }

    @Test
    public void testWithActionGroup() throws TimeoutException, ExecutionException {
        AtomicInteger counter = new AtomicInteger(0);

        BackgroundAction actionA = new BackgroundAction(20, TimeUnit.MILLISECONDS);

        actionA.addRunnable(counter::incrementAndGet);

        actionA.setPeriodDelayAfterExecution(false);

        int expectedValue = 40;

        DelayedAction mainAction = new DelayedAction(20 * expectedValue, TimeUnit.MILLISECONDS);

        ActionGroup group = new ActionGroup()
                .with(actionA)
                .addSequential(mainAction);

        TimeWarpedSimulation sim = new TimeWarpedSimulation(1);

        sim.add(group);

        sim.runSimulation(10, TimeUnit.SECONDS);

        assertEquals(expectedValue, counter.get(), expectedValue * (19F / 20F));
    }

    @Test // TODO: add
    public void testTimedAction() {
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

    @Test
    public void testUpdateableGroup() {
        List<Integer> list = new ArrayList<>();
        Updateable one = () -> list.add(1);
        Updateable two = () -> list.add(2);
        Updateable three = () -> list.add(3);
        Updateable four = () -> list.add(4);

        UpdateableGroup group = new UpdateableGroup(one, two, three, four);
        assertTrue(group.update());
        assertEquals(list, Arrays.asList(1, 2, 3, 4));
        list.clear();
        group.remove(three);
        assertTrue(group.update());
        assertEquals(list, Arrays.asList(1, 2, 4));
        list.clear();
        group.remove(one);
        group.remove(two);
        group.remove(four);
        assertFalse(group.update());
        assertTrue(list.isEmpty());
    }


    @Test
    public void testKillingActionGroups() throws Exception {
        TimeWarpedClock clock = new TimeWarpedClock(10);

        MainActionScheduler actionScheduler = new MainActionScheduler(clock);

        AtomicDouble counter = new AtomicDouble();

        ActionGroup actionGroup = new ActionGroup()
                .addSequential(() -> counter.getAndAdd(1))
                .addSequential(new TimedPeriodicAction(250, TimeUnit.MILLISECONDS))


                .addSequential(() -> counter.getAndAdd(1))

                .addSequential(new TimedPeriodicAction(1000, TimeUnit.MILLISECONDS))

                .addSequential(() -> counter.getAndAdd(1))
                .addSequential(new TimedPeriodicAction(250, TimeUnit.MILLISECONDS))

                .addSequential(() -> counter.getAndAdd(1));

        Future<Void> voidFuture = actionScheduler.scheduleAction(actionGroup);

        while (true) {
            if (counter.get() == 2) {
                voidFuture.cancel(true);
                break;
            }
        }
        assertEquals(2, counter.get());
        Thread.sleep(1000);

        assertEquals(2, counter.get());
    }
}
