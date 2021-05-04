package com.github.ezauton.core.purepursuit

import com.github.ezauton.conversion.*
import com.github.ezauton.core.action.*
import com.github.ezauton.core.localization.estimators.TankRobotEncoderEncoderEstimator
import com.github.ezauton.core.pathplanning.Trajectory
import com.github.ezauton.core.pathplanning.TrajectoryGenerator
import com.github.ezauton.core.pathplanning.purepursuit.LookaheadBounds
import com.github.ezauton.core.pathplanning.purepursuit.PPWaypoint
import com.github.ezauton.core.record.recording
import com.github.ezauton.core.record.save
import com.github.ezauton.core.robot.implemented.TankRobotTransLocDrivable
import com.github.ezauton.core.simulation.SimulatedTankRobot
import com.github.ezauton.core.simulation.parallel
import com.github.ezauton.core.utils.RealClock
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeoutException

class PPSimulatorTest {

  @Throws(TimeoutException::class, ExecutionException::class)
  fun testLeftToRightScale() {
    val build = PPWaypoint.Builder()
      .add(0.m, 0.m, 16.mps, 13.0.mpss, -12.0.mpss)
      .add(0.m, 4.0.m, 16.0.mps, 13.0.mpss, -12.0.mpss)
      .add(-0.5.m, 8.589.m, 16.0.mps, 13.0.mpss, -12.0.mpss)
      .add(-0.5.m, 12.405.m, 13.0.mps, 13.0.mpss, -12.0.mpss)
      .add(-0.5.m, 17.0.m, 8.5.mps, 13.0.mpss, -12.0.mpss)
      .add(1.5.m, 19.4.m, 0.0.mps, 13.0.mpss, -12.0.mpss)
      .buildArray()

    test("testLeftToRightScale", *build)
  }

  @Test
  @Throws(TimeoutException::class, ExecutionException::class)
  fun testStraight() {


    val waypoint1 = PPWaypoint.simple2D(0.m, 0.m, 0.0.mps, 3.0.mpss, (-4.0).mpss)
    val waypoint2 = PPWaypoint.simple2D(0.m, 6.m, 5.0.mps, 3.0.mpss, (-4.0).mpss)
    val waypoint3 = PPWaypoint.simple2D(0.m, 20.m, 0.0.mps, 3.0.mpss, (-4.0).mpss)

    test("testStraight", waypoint1, waypoint2, waypoint3)
  }

//  @Test TODO: add back
//  @Throws(TimeoutException::class, ExecutionException::class)
//  fun testStraightGeneric() {
//    test("testStraightGeneric", PathHelper.STRAIGHT_12UNITS)
//  }

  @Test
  @Throws(TimeoutException::class, ExecutionException::class)
  fun testRight() {
    val waypoint1 = PPWaypoint.simple2D(0.m, 0.m, 0.mps, 3.mpss, (-3.0).mpss)
    val waypoint2 = PPWaypoint.simple2D(6.m, 6.m, 5.mps, 3.mpss, (-3.0).mpss)
    val waypoint3 = PPWaypoint.simple2D(12.m, 0.m, 0.mps, 3.mpss, (-3.0).mpss)

    test("testRight", waypoint1, waypoint2, waypoint3)
  }

//  @Test TODO: re-enable
//  @Throws(TimeoutException::class, ExecutionException::class)
//  fun testSpline() {
//    test(
//      "testSpline", SplinePPWaypoint.Builder()
//        .add(0.0, 0.0, 0.0, 15.0, 13.0, -12.0)
//        .add(0.0, 13.0, 0.0, 10.0, 13.0, -12.0)
//        .add(20.0, 17.0, -Math.PI / 2, 8.0, 13.0, -12.0)
//        .add(23.0, 24.0, 0.0, 0.5, 13.0, -12.0)
//        .buildPathGenerator()
//        .generate(0.05)
//    )
//  }


  @Throws(TimeoutException::class, ExecutionException::class)
  private fun test(name: String, trajectory: Trajectory) {

    // Might be a problem
    val simulatedRobot = SimulatedTankRobot(1.m, RealClock, 14.0.mpss, 0.3.mps, 16.0.mps)
    simulatedRobot.defaultLocEstimator.reset()
    val leftMotor = simulatedRobot.leftMotor
    val rightMotor = simulatedRobot.rightMotor

    val locationEstimator = TankRobotEncoderEncoderEstimator(simulatedRobot.leftDistanceSensor, simulatedRobot.rightDistanceSensor, simulatedRobot)
    locationEstimator.reset()

    val lookahead = LookaheadBounds(1.0.m, 5.0.m, 2.0.mps, 10.0.mps, locationEstimator)


    val updateKinematics = action {
      simulatedRobot.update()
      locationEstimator.update()
    }


    val drivable = TankRobotTransLocDrivable(leftMotor, rightMotor, locationEstimator, locationEstimator, simulatedRobot)

    val purePursuit = purePursuit(Periodic(5.ms, before = updateKinematics), trajectory, locationEstimator, drivable, lookahead)

    val action = action {
      val recording = ephemeral {
        val builder = recording {
          include(trajectory.path.simpleRepr)
          sample(5.ms, locationEstimator)
          parallel(purePursuit)
        }

        delay(10.seconds)
        builder.build()
      }

      println("saving")
      recording.save("test.json")
    }

    runBlocking {
      withTimeout(15.seconds) {
        action.run()
      }
    }


    val leftWheelVelocity = locationEstimator.leftTranslationalWheelVelocity
    assertTrue(leftWheelVelocity in (-0.5).mps..0.5.mps)

    val rightWheelVelocity = locationEstimator.rightTranslationalWheelVelocity
    assertTrue(rightWheelVelocity in (-0.5).mps..0.5.mps)

    // The final location after the simulator
    val finalLoc = locationEstimator.estimateLocation()

    // If the final loc is approximately equal to the last waypoint
    approxEqual(trajectory.path.end, finalLoc, 0.2)

    // If the final loc is approximately equal to the last waypoint
    approxEqual(trajectory.path.end, finalLoc, 0.2)
  }

  /**
   * Test the path with a robot max acceleration 14ft/s^2, min velocity 0.3ft/s, maxVelocity 16ft/s
   *
   * @param waypoints
   */
  @Throws(TimeoutException::class, ExecutionException::class)
  private fun test(name: String, vararg waypoints: PPWaypoint) {
    val pathGenerator = TrajectoryGenerator(*waypoints)
    val trajectory = pathGenerator.generate(0.05.seconds)
    test(name, trajectory)
  }

  private fun <T : SIUnit<T>> approxEqual(a: ConcreteVector<T>, b: ConcreteVector<T>, epsilon: Double) {
    return approxEqual(a.scalarVector, b.scalarVector, epsilon)
  }

  private fun approxEqual(a: ScalarVector, b: ScalarVector, epsilon: Double) {
    val bElements = b.elements
    val aElements = a.elements
    for (i in aElements.indices) {
      assertEquals(aElements[i], bElements[i], epsilon)
    }
  }

}
