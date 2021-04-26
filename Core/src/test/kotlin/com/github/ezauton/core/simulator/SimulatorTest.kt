//package com.github.ezauton.core.simulator
//
//import com.github.ezauton.core.action.ActionGroup
//import com.github.ezauton.core.action.DelayedAction
//import com.github.ezauton.core.localization.estimators.TankRobotEncoderEncoderEstimator
//import com.github.ezauton.core.simulation.SimulatedTankRobot
//import com.github.ezauton.core.simulation.TimeWarpedSimulation
//import com.github.ezauton.core.utils.ManualClock
//import org.junit.jupiter.api.Assertions.*
//import org.junit.jupiter.api.Test
//import java.util.concurrent.ExecutionException
//import java.util.concurrent.TimeUnit
//import java.util.concurrent.TimeoutException
//import java.util.concurrent.atomic.AtomicBoolean
//import java.util.concurrent.atomic.AtomicInteger
//
//class SimulatorTest {
//
//  @Test
//  @Throws(TimeoutException::class, ExecutionException::class)
//  fun testSimpleAction() {
//    val atomicBoolean = AtomicBoolean(false)
//    val simulation = TimeWarpedSimulation()
//    simulation.add(BaseAction({ atomicBoolean.set(true) }))
//    simulation.runSimulation(100, TimeUnit.SECONDS)
//    assertTrue(atomicBoolean.get())
//  }
//
//  @Test
//  @Throws(TimeoutException::class, ExecutionException::class)
//  fun testDelayedAction() {
//    val atomicBoolean = AtomicBoolean(false)
//    val simulation = TimeWarpedSimulation()
//    val delayedAction = DelayedAction(1, TimeUnit.SECONDS, { atomicBoolean.set(true) })
//    simulation.add(delayedAction)
//    simulation.runSimulation(100, TimeUnit.SECONDS)
//    assertTrue(atomicBoolean.get())
//  }
//
//  @Test
//  @Throws(TimeoutException::class, ExecutionException::class)
//  fun testActionGroup() {
//    val atomicInteger = AtomicInteger(0)
//
//    val simulation = TimeWarpedSimulation(10.0)
//    val actionGroup = ActionGroup()
//
//    val delayedAction = DelayedAction(1, TimeUnit.SECONDS, { atomicInteger.compareAndSet(2, 3) })
//    delayedAction.onFinish({ })
//
//    val delayedAction2 = DelayedAction(10, TimeUnit.MILLISECONDS, { atomicInteger.compareAndSet(0, 1) })
//    delayedAction2.onFinish({ })
//
//    val delayedAction3 = DelayedAction(500, TimeUnit.MILLISECONDS, { atomicInteger.compareAndSet(1, 2) })
//    delayedAction3.onFinish({ })
//
//    // TODO: Order matters? See github #35
//    actionGroup.addParallel(delayedAction3) // second
//    actionGroup.with(delayedAction2) // first
//    actionGroup.addSequential(delayedAction) // last
//
//    simulation.add(actionGroup)
//    simulation.runSimulation(100, TimeUnit.SECONDS)
//    assertEquals(3, atomicInteger.get())
//  }
//
//  @Test
//  fun testStraight() {
//    val clock = ManualClock()
//    val robot = SimulatedTankRobot(1.0, clock, 14.0, 0.3, 16.0)
//    val encoderRotationEstimator = TankRobotEncoderEncoderEstimator(robot.leftDistanceSensor, robot.rightDistanceSensor, robot)
//    encoderRotationEstimator.reset()
//    for (i in 0..999) {
//      robot.run(1.0, 1.0)
//      encoderRotationEstimator.update()
//      clock.incAndGet()
//    }
//    //        System.out.println("encoderRotationEstimator = " + encoderRotationEstimator.estimateLocation());
//  }
//
//  @Test
//  @Throws(InterruptedException::class)
//  fun testTimeout() {
//    val atomicInteger = AtomicInteger(0)
//
//    val simulation = TimeWarpedSimulation(1.0)
//    val actionGroup = ActionGroup()
//
//    val action = BackgroundAction(20, TimeUnit.MILLISECONDS, Runnable { atomicInteger.incrementAndGet() })
//
//    // TODO: Order matters? See github #35
//    actionGroup.addSequential(action)
//
//    simulation.add(actionGroup)
//
//    assertThrows(TimeoutException::class.java) { simulation.runSimulation(1, TimeUnit.SECONDS) }
//
//    val actual = atomicInteger.get()
//    assertEquals(50f, actual.toFloat(), 2f)
//    Thread.sleep(500) // other threads should have stopped, no more incrementing
//    //        assertEquals(2, actual, atomicInteger.get());
//  }
//}
