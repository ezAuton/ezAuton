//package com.github.ezauton.core.util
//
//import com.github.ezauton.core.action.Action
//import com.github.ezauton.core.action.ActionGroup
//import com.github.ezauton.core.action.DelayedAction
//import com.github.ezauton.core.action.TimedPeriodicAction
//import com.github.ezauton.core.localization.UpdatableGroup
//import com.github.ezauton.core.simulation.TimeWarpedSimulation
//import com.github.ezauton.core.utils.RealClock
//import com.github.ezauton.core.utils.Stopwatch
//import com.github.ezauton.core.utils.TimeWarpedClock
//import com.google.common.util.concurrent.AtomicDouble
//import org.junit.jupiter.api.Assertions.*
//import org.junit.jupiter.api.Test
//import java.util.*
//import java.util.concurrent.CancellationException
//import java.util.concurrent.ExecutionException
//import java.util.concurrent.TimeUnit
//import java.util.concurrent.TimeoutException
//import java.util.concurrent.atomic.AtomicBoolean
//import java.util.concurrent.atomic.AtomicInteger
//import java.util.concurrent.atomic.AtomicLong
//
//class ActionTest2 {
//
//  private val actionScheduler = MainActionScheduler(RealClock.CLOCK)
//
//  @Test
//  @Throws(InterruptedException::class, TimeoutException::class, ExecutionException::class)
//  fun testScheduleActionInterface() {
//    val atomicLong = AtomicLong(0)
//    val action = object : Action {
//
//      // Not implemented
//      val finished: List<Runnable>
//        get() = emptyList()
//
//      fun run(actionRunInfo: ActionRunInfo) {
//        atomicLong.set(actionRunInfo.getClock().getTime())
//      }
//
//      fun end() {
//        // Not implemented
//      }
//
//      fun onFinish(onFinish: Runnable): Action {
//        return this // Not implemented
//      }
//    }
//
//    actionScheduler.scheduleAction(action).get(1000, TimeUnit.MILLISECONDS)
//
//    assertEquals(System.currentTimeMillis().toFloat(), atomicLong.get().toFloat(), 1000f)
//  }
//
//  @Test
//  fun testDelayedActionInterrupt() {
//    val atomicBoolean = AtomicBoolean(false)
//
//    val delayedAction = DelayedAction(20, TimeUnit.SECONDS, { atomicBoolean.set(true) })
//    val voidFuture = actionScheduler.scheduleAction(delayedAction)
//    voidFuture.cancel(true)
//    assertThrows(CancellationException::class.java) { voidFuture.get(1000, TimeUnit.MILLISECONDS) }
//    assertFalse(atomicBoolean.get())
//  }
//
//  @Test
//  @Throws(TimeoutException::class, ExecutionException::class)
//  fun testDelayedAction() {
//    val sim = TimeWarpedSimulation(10.0)
//
//    val delay = 3
//    val action = DelayedAction(delay, TimeUnit.SECONDS) // w
//    //        action.onFinish(() -> System.out.println("[testDelayedAction] The delayed action finished"));
//
//    sim.add(action)
//
//    val stopwatch = Stopwatch(RealClock.CLOCK)
//
//    stopwatch.resetIfNotInit()
//    sim.runSimulation(2, TimeUnit.SECONDS)
//    assertEquals(delay.toDouble(), stopwatch.pop(TimeUnit.SECONDS) * 10, 0.2)
//  }
//
//  @Test
//  @Throws(ExecutionException::class, TimeoutException::class, InterruptedException::class)
//  fun testActionGroupSingleNoSim() {
//
//    val clock = TimeWarpedClock(10.0)
//
//    val actionScheduler = MainActionScheduler(clock)
//
//    val count = AtomicInteger(0)
//    count.compareAndSet(0, 1)
//    assertEquals(1, count.get())
//
//    val action = DelayedAction(3, TimeUnit.SECONDS, { count.compareAndSet(1, 3) })
//    action.onFinish({ count.compareAndSet(3, 4) })
//    val group = ActionGroup()
//      .addSequential(action)
//
//    val voidFuture = actionScheduler.scheduleAction(group)
//    voidFuture.get(10, TimeUnit.SECONDS)
//    assertEquals(4, count.get())
//  }
//
//  @Test
//  @Throws(TimeoutException::class, ExecutionException::class)
//  fun testActionGroupSingle() {
//    val count = AtomicInteger(0)
//    count.compareAndSet(0, 1)
//    assertEquals(1, count.get())
//    val action = DelayedAction(3, TimeUnit.SECONDS)
//    action.onFinish { count.compareAndSet(1, 4) }
//    val group = ActionGroup()
//      .addSequential(action)
//
//    val sim = TimeWarpedSimulation(10.0)
//    sim.add(group)
//    sim.runSimulation(3, TimeUnit.SECONDS)
//    assertEquals(4, count.get())
//  }
//
//  @Test
//  @Throws(InterruptedException::class, ExecutionException::class, TimeoutException::class)
//  fun testActionGroupSequentialThreadBuilder() {
//    val count = AtomicInteger(0)
//
//    val two = DelayedAction(200, TimeUnit.MILLISECONDS)
//    two.onFinish {
//      //            System.out.println("Two finished");
//      count.compareAndSet(0, 2)
//    }
//
//    val three = DelayedAction(300, TimeUnit.MILLISECONDS)
//    two.onFinish {
//      //            System.out.println("three finished");
//      count.compareAndSet(2, 3)
//    }
//
//    val group = ActionGroup()
//      .addSequential(two)
//      .addSequential(three)
//      .onFinish {
//        //                    System.out.println("all done");
//
//        count.compareAndSet(3, 4)
//      }
//
//    //        new ProcessBuilder(group).startAndWait(1, TimeUnit.SECONDS);
//    actionScheduler.scheduleAction(group).get(1, TimeUnit.SECONDS)
//    assertEquals(4, count.get())
//  }
//
//  @Test
//  @Throws(TimeoutException::class, ExecutionException::class)
//  fun testActionGroup() {
//    val count = AtomicInteger(0)
//
//    val five = DelayedAction(5, TimeUnit.SECONDS) // then
//    five.onFinish { count.compareAndSet(3, 5) }
//    //        five.onFinish(() -> System.out.println("five finished"));
//
//    val three1 = DelayedAction(3, TimeUnit.SECONDS) // first
//    three1.onFinish { count.compareAndSet(0, 3) }
//    //        three1.onFinish(() -> System.out.println("three1 finished"));
//
//    val three2 = DelayedAction(3, TimeUnit.SECONDS) // last
//    three2.onFinish { count.compareAndSet(5, 8) }
//    //        three2.onFinish(() -> System.out.println("three2 finished"));
//
//    // three1 and five run in parallel
//    // when five has 2 seconds left, three2 starts
//    // then finally actiongroup should terminate
//
//    val actionGroup = ActionGroup()
//      .addParallel(three1)
//      .addSequential(five)
//      .addSequential(three2)
//      .onFinish {
//        //                    System.out.println("done");
//        count.compareAndSet(8, 10)
//      }
//
//    val simulation = TimeWarpedSimulation(10.0)
//    simulation.add(actionGroup)
//    simulation.runSimulation(100, TimeUnit.SECONDS)
//
//    assertEquals(10, count.toInt())
//  }
//
//  @Test
//  @Throws(TimeoutException::class, ExecutionException::class)
//  fun testActionGroupSequential() {
//    val count = AtomicInteger(0)
//
//    val two = DelayedAction(2, TimeUnit.SECONDS)
//    two.onFinish {
//      //            System.out.println("Two finished");
//      count.compareAndSet(0, 2)
//    }
//
//    val three = DelayedAction(3, TimeUnit.SECONDS)
//    two.onFinish {
//      //            System.out.println("three finished");
//      count.compareAndSet(2, 3)
//    }
//
//    val group = ActionGroup()
//      .addSequential(two)
//      .addSequential(three)
//      .onFinish {
//        //                    System.out.println("all done");
//
//        count.compareAndSet(3, 4)
//      }
//
//    val simulation = TimeWarpedSimulation()
//    simulation.add(group)
//    simulation.runSimulation(100, TimeUnit.SECONDS)
//
//    assertEquals(4, count.get())
//  }
//
//  @Test
//  @Throws(TimeoutException::class, ExecutionException::class)
//  fun testWithActionGroup() {
//    val counter = AtomicInteger(0)
//
//    val actionA = BackgroundAction(20, TimeUnit.MILLISECONDS)
//
//    actionA.addRunnable(Runnable { counter.incrementAndGet() })
//
//    actionA.isPeriodDelayAfterExecution = false
//
//    val expectedValue = 40
//
//    val mainAction = DelayedAction(20 * expectedValue, TimeUnit.MILLISECONDS)
//
//    val group = ActionGroup()
//      .with(actionA)
//      .addSequential(mainAction)
//
//    val sim = TimeWarpedSimulation(1.0)
//
//    sim.add(group)
//
//    sim.runSimulation(10, TimeUnit.SECONDS)
//
//    assertEquals(expectedValue.toFloat(), counter.get().toFloat(), expectedValue * (19f / 20f))
//  }
//
//  @Test // TODO: add
//  fun testTimedAction() {
//    //        AtomicLong count = new AtomicLong(0);
//    //
//    //        long expectedDTms = 3000;
//    //        TimedPeriodicAction action = new TimedPeriodicAction(TimeUnit.MILLISECONDS, expectedDTms, () -> count.set(System.currentTimeMillis()));
//    //
//    //        Simulation sim = new Simulation(1);
//    //        sim.add(action);
//    //
//    //        long initmillis = System.currentTimeMillis();
//    //        sim.run(TimeUnit.SECONDS, 12);
//    //        System.out.println("count.get() - initmillis = " + (count.get() - initmillis));
//    //        assertEquals(expectedDTms, count.get() - initmillis, 50);
//  }
//
//  @Test
//  fun testUpdateableGroup() {
//    val list = ArrayList<Int>()
//    val one = { list.add(1) }
//    val two = { list.add(2) }
//    val three = { list.add(3) }
//    val four = { list.add(4) }
//
//    val group = UpdatableGroup(one, two, three, four)
//    assertTrue(group.update())
//    assertEquals(list, Arrays.asList(1, 2, 3, 4))
//    list.clear()
//    group.remove(three)
//    assertTrue(group.update())
//    assertEquals(list, Arrays.asList(1, 2, 4))
//    list.clear()
//    group.remove(one)
//    group.remove(two)
//    group.remove(four)
//    assertFalse(group.update())
//    assertTrue(list.isEmpty())
//  }
//
//  @Test
//  @Throws(Exception::class)
//  fun testKillingActionGroups() {
//    val clock = TimeWarpedClock(10.0)
//
//    val actionScheduler = MainActionScheduler(clock)
//
//    val counter = AtomicDouble()
//
//    val actionGroup = ActionGroup()
//      .addSequential({ counter.getAndAdd(1.0) })
//      .addSequential(TimedPeriodicAction(250, TimeUnit.MILLISECONDS))
//
//      .addSequential({ counter.getAndAdd(1.0) })
//
//      .addSequential(TimedPeriodicAction(1000, TimeUnit.MILLISECONDS))
//
//      .addSequential({ counter.getAndAdd(1.0) })
//      .addSequential(TimedPeriodicAction(250, TimeUnit.MILLISECONDS))
//
//      .addSequential({ counter.getAndAdd(1.0) })
//
//    val voidFuture = actionScheduler.scheduleAction(actionGroup)
//
//    while (true) {
//      if (counter.get() == 2.0) {
//        voidFuture.cancel(true)
//        break
//      }
//    }
//    assertEquals(2.0, counter.get())
//    Thread.sleep(1000)
//
//    assertEquals(2.0, counter.get())
//  }
//}
