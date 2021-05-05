package com.github.ezauton.core.purepursuit

import com.github.ezauton.conversion.*
import com.github.ezauton.core.action.action
import com.github.ezauton.core.action.maxDuration
import com.github.ezauton.core.action.purePursuit
import com.github.ezauton.core.action.withTimeout
import com.github.ezauton.core.pathplanning.Trajectory
import com.github.ezauton.core.pathplanning.purepursuit.PPWaypoint
import com.github.ezauton.core.pathplanning.purepursuit.ScalingLookahead
import com.github.ezauton.core.pathplanning.purepursuit.scalingLookahead
import com.github.ezauton.core.pathplanning.purepursuit.trajectory
import com.github.ezauton.core.record.recording
import com.github.ezauton.core.record.save
import com.github.ezauton.core.simulation.SimulatedTankRobot
import com.github.ezauton.core.simulation.parallel
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeoutException

suspend fun run() {

  // a straight line trajectory starting at (0,0) going to (0,20) with a max speed of 3 m/s.
  val trajectory = trajectory(samplePeriod = 5.ms) {
    point(0.m, 0.m, speed = 0.mps, acceleration = 13.0.mps / s, deceleration = 12.0.mps / s)
    point(0.m, 10.m, speed = 3.mps, acceleration = 13.0.mps / s, deceleration = 12.0.mps / s)
    point(0.m, 20.m, speed = 0.mps, acceleration = 13.0.mps / s, deceleration = 12.0.mps / s)
  }

  // a simulated robot
  val robot = SimulatedTankRobot.create(lateralWheelDistance = 1.m, maxAccel = 14.0.mpss, minVel = 0.3.mps, maxVel = 16.0.mps)

  // a lookahead that scales with the velocity of the robot
  val lookahead = ScalingLookahead(distanceRange = 1.0.m..5.0.m, speedRange = 2.0.mps..10.0.mps, velocityEstimator = robot)

  // pure pursuit
  val purePursuit = robot.purePursuit(period = 10.ms, trajectory, lookahead)

  // the action we will actually be running
  val action = action {
    val recording = maxDuration(10.seconds) {

      // record everything inside this
      recording {

        // include data about the path
        include(trajectory.path.simpleRepr)

        // run pure pursuit in parallel
        parallel(purePursuit)

        // every 10ms sample data from the robot (like location) and include in recording
        sample(10.ms, robot)
      }
    }

    // save the recording to ~/.ezauton/test.json
    recording.save("test.json")
  }

  // run the action
  action.run()

}
//
//
//
//class PPSimulatorTest {
//
//
//  @Throws(TimeoutException::class, ExecutionException::class)
//  fun testLeftToRightScale() {
//    val t = trajectory(0.5.ms) {
//      point(0.m, 0.m, 16.mps, 13.0.mpss, -12.0.mpss)
//      point(0.m, 4.0.m, 16.0.mps, 13.0.mpss, -12.0.mpss)
//      point(-0.5.m, 8.589.m, 16.0.mps, 13.0.mpss, -12.0.mpss)
//      point(-0.5.m, 12.405.m, 13.0.mps, 13.0.mpss, -12.0.mpss)
//      point(-0.5.m, 17.0.m, 8.5.mps, 13.0.mpss, -12.0.mpss)
//      point(1.5.m, 19.4.m, 0.0.mps, 13.0.mpss, -12.0.mpss)
//    }
//
//    test(t)
//  }
//
//  @Test
//  @Throws(TimeoutException::class, ExecutionException::class)
//  fun testStraight() {
//
//    val waypoint1 = PPWaypoint.simple2D(0.m, 0.m, 0.0.mps, 3.0.mpss, (-4.0).mpss)
//    val waypoint2 = PPWaypoint.simple2D(0.m, 6.m, 5.0.mps, 3.0.mpss, (-4.0).mpss)
//    val waypoint3 = PPWaypoint.simple2D(0.m, 20.m, 0.0.mps, 3.0.mpss, (-4.0).mpss)
//
////    test("testStraight", waypoint1, waypoint2, waypoint3)
//  }
//
////  @Test TODO: add back
////  @Throws(TimeoutException::class, ExecutionException::class)
////  fun testStraightGeneric() {
////    test("testStraightGeneric", PathHelper.STRAIGHT_12UNITS)
////  }
//
//  @Test
//  @Throws(TimeoutException::class, ExecutionException::class)
//  fun testRight() {
//    val waypoint1 = PPWaypoint.simple2D(0.m, 0.m, 0.mps, 3.mpss, (-3.0).mpss)
//    val waypoint2 = PPWaypoint.simple2D(6.m, 6.m, 5.mps, 3.mpss, (-3.0).mpss)
//    val waypoint3 = PPWaypoint.simple2D(12.m, 0.m, 0.mps, 3.mpss, (-3.0).mpss)
//
////    test("testRight", waypoint1, waypoint2, waypoint3)
//  }
//
////  @Test TODO: re-enable
////  @Throws(TimeoutException::class, ExecutionException::class)
////  fun testSpline() {
////    test(
////      "testSpline", SplinePPWaypoint.Builder()
////        .add(0.0, 0.0, 0.0, 15.0, 13.0, -12.0)
////        .add(0.0, 13.0, 0.0, 10.0, 13.0, -12.0)
////        .add(20.0, 17.0, -Math.PI / 2, 8.0, 13.0, -12.0)
////        .add(23.0, 24.0, 0.0, 0.5, 13.0, -12.0)
////        .buildPathGenerator()
////        .generate(0.05)
////    )
////  }
//
//
//  private fun test(trajectory: Trajectory) {
//
//    val robot = SimulatedTankRobot.create(lateralWheelDistance = 1.m, maxAccel = 14.0.mpss, minVel = 0.3.mps, maxVel = 16.0.mps)
//    val lookahead = ScalingLookahead(1.0.m, 5.0.m, 2.0.mps, 10.0.mps, robot)
//
//    val purePursuit = robot.purePursuit(period = 10.ms, trajectory, lookahead)
//
//    val action = action {
//      val recording = maxDuration(10.seconds) {
//        recording {
//          include(trajectory.path.simpleRepr)
//          parallel(purePursuit)
//          sample(10.ms, robot)
//        }
//      }
//
//      println("saving")
//      recording.save("test.json")
//    }
//
//    runBlocking {
//      withTimeout(15.seconds) {
//        action.run()
//      }
//    }
//
//
//    val leftWheelVelocity = robot.leftTranslationalWheelVelocity
//    assertTrue(leftWheelVelocity in (-0.5).mps..0.5.mps)
//
//    val rightWheelVelocity = robot.rightTranslationalWheelVelocity
//    assertTrue(rightWheelVelocity in (-0.5).mps..0.5.mps)
//
//    // The final location after the simulator
//    val finalLoc = robot.estimateLocation()
//
//    // If the final loc is approximately equal to the last waypoint
//    approxEqual(trajectory.path.end, finalLoc, 0.2)
//
//    // If the final loc is approximately equal to the last waypoint
//    approxEqual(trajectory.path.end, finalLoc, 0.2)
//  }
//
////  /**
////   * Test the path with a robot max acceleration 14ft/s^2, min velocity 0.3ft/s, maxVelocity 16ft/s
////   *
////   * @param waypoints
////   */
////  @Throws(TimeoutException::class, ExecutionException::class)
////  private fun test(name: String, vararg waypoints: PPWaypoint) {
////    val pathGenerator = TrajectoryGenerator(*waypoints)
////    val trajectory = pathGenerator.generate(0.05.seconds)
////    test(name, trajectory)
////  }
//
//  private fun <T : SIUnit<T>> approxEqual(a: ConcreteVector<T>, b: ConcreteVector<T>, epsilon: Double) {
//    return approxEqual(a.scalarVector, b.scalarVector, epsilon)
//  }
//
//  private fun approxEqual(a: ScalarVector, b: ScalarVector, epsilon: Double) {
//    val bElements = b.elements
//    val aElements = a.elements
//    for (i in aElements.indices) {
//      assertEquals(aElements[i], bElements[i], epsilon)
//    }
//  }
//
//}
