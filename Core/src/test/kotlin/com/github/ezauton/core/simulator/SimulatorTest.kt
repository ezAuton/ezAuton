package com.github.ezauton.core.simulator

import com.github.ezauton.conversion.*
import com.github.ezauton.core.action.*
import com.github.ezauton.core.simulation.SimulatedTankRobot
import com.github.ezauton.core.simulation.parallel
import com.github.ezauton.core.simulation.sequential
import com.github.ezauton.core.utils.RealClock
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicInteger

class SimulatorTest {

  @Test
  @Throws(TimeoutException::class, ExecutionException::class)
  fun testSimpleAction() = runBlocking {
    var bool = false
    val action = action {
      bool = true
    }

    action.runWithTimeout(1.seconds)

    assertTrue(bool)
  }

  @Test
  @Throws(TimeoutException::class, ExecutionException::class)
  fun testDelayedAction() = runBlocking {

    var bool = false

    val delayedAction = action {
      delay(1.seconds)
      bool = true
    }

    delayedAction.runWithTimeout(2.seconds)

    assertTrue(bool)
  }

  @Test
  @Throws(TimeoutException::class, ExecutionException::class)
  fun testActionGroup() = runBlocking {
    val atomicInteger = AtomicInteger(0)

    val delayedAction = action {
      delay(1.seconds)
      atomicInteger.compareAndSet(2, 3)
      return@action
    }

    val delayedAction2 = action {
      delay(10.ms)
      atomicInteger.compareAndSet(0, 1)
      return@action
    }

    val delayedAction3 = action {
      delay(500.ms)
      atomicInteger.compareAndSet(1, 2)
      return@action
    }


    val action = action {
      parallel(delayedAction3)
      ephemeralScope {
        parallel(delayedAction2)
        sequential(delayedAction)
      }
    }

    action.runWithTimeout(10.seconds)
    assertEquals(3, atomicInteger.get())
  }

  @Test
  fun testStraight() = runBlocking {

    val clock = RealClock
    val robot = SimulatedTankRobot(1.0.meters, clock, 14.0.mpss, 0.3.mps, 16.0.mps)

    for (i in 0..100) {
      robot.run(1.0.mps, 1.0.mps)
      delay(10.ms)
    }

    assertTrue(robot.locationEstimator.estimateLocation().y > 1.m)
    assertTrue(robot.locationEstimator.estimateLocation().x.abs() < 0.01.m)


  }

  @Test
  @Throws(InterruptedException::class)
  fun testTimeout() {
    val atomicInteger = AtomicInteger(0)


    val action = periodicAction(20.ms) {
      atomicInteger.incrementAndGet()
    }

    val actionGroup = action {
      sequential(action)
    }

    assertThrows(TimeoutCancellationException::class.java) {
      runBlocking {
        actionGroup.runWithTimeout(1.seconds)
      }
    }

    runBlocking {
      val actual = atomicInteger.get()
      assertEquals(50f, actual.toFloat(), 2f)
      delay(500.ms) // other threads should have stopped, no more incrementing
      assertEquals(2.0f, actual.toFloat(), atomicInteger.get().toFloat())

    }
  }
}
