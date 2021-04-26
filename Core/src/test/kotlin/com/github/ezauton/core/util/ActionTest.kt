//package com.github.ezauton.core.util
//
//import com.github.ezauton.conversion.ms
//import com.github.ezauton.conversion.seconds
//import com.github.ezauton.core.action.*
//import com.github.ezauton.core.utils.TimeWarpedClock
//import org.junit.jupiter.api.Assertions.assertEquals
//import org.junit.jupiter.api.Test
//import java.util.concurrent.TimeUnit
//import java.util.concurrent.TimeoutException
//
//class ActionTest {
//
//
//  @Test
//  fun `overloaded periodic tries to become stable`() {
//    var bool = false
//
//
//    action {
//      var someValue = 2;
//      periodic(10.ms, duration = 10.seconds) { loop ->
//        someValue *= someValue
//        if (someValue > 1000) {
//          loop.stop()
//          println("this doesn't work")
//        }
//
//        println("someValue is $someValue")
//      }
//    }
//
//
//    val timedPeriodicAction = TimedPeriodicAction(20, TimeUnit.MILLISECONDS, 2,
//      TimeUnit.SECONDS, Runnable {
//        if (!bool) {
//          bool = true
//          Thread.sleep(1_000)
//        }
//      })
//
//    try {
//      scheduler.scheduleAction(timedPeriodicAction).get(2_000, TimeUnit.MILLISECONDS)
//    } catch (ignored: TimeoutException) {
//    }
//
//    assertEquals(2_000 / 20.toDouble(), timedPeriodicAction.timesRun.toDouble(), 2.toDouble())
//  }
//
//  @Test
//  fun `action group of parallels test`() {
//
//    var counter = 0
//
//    val actionGroup = ActionGroup.ofParallels(
//      BaseAction { counter++ },
//      BaseAction { counter++ },
//      BaseAction { counter++ }
//    )
//
//    ModernSimulatedClock()
//      .add(actionGroup)
//      .runSimulation(1, TimeUnit.SECONDS)
//    assertEquals(3, counter)
//  }
//
//  @Test
//  fun `action group of wrappers test`() {
//
//    var counter = 0
//
//    val action = action {
//
//      condition {
//
//      }
//      periodic(10.ms, resourceManagement = ResourceManagement.UNTIL_FINISH) {
//
//      }
//    }
//
//    val actionGroup = ActionGroup(
//
//      DelayedAction(30, TimeUnit.SECONDS)
//        .onFinish { if (counter == 0) counter++ }
//        .wrapType(ActionGroup.Type.WITH),
//
//      DelayedAction(20, TimeUnit.SECONDS)
//        .onFinish { if (counter == 1) counter++ }
//        .wrapType(ActionGroup.Type.PARALLEL),
//
//      DelayedAction(10, TimeUnit.SECONDS)
//        .wrapType(ActionGroup.Type.SEQUENTIAL)
//    )
//
//    val clock = TimeWarpedClock(10.0, 0)
//
//    val warpedScheduler = MainActionScheduler(clock)
//
//    warpedScheduler.scheduleAction(actionGroup).get(4_000, TimeUnit.MILLISECONDS)
//
//    assertEquals(2, counter)
//  }
//}
