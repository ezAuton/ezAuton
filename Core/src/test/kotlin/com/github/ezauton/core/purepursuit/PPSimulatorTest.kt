package com.github.ezauton.core.purepursuit

import com.github.ezauton.conversion.*
import com.github.ezauton.core.action.purePursuit
import com.github.ezauton.core.localization.estimators.TankRobotEncoderEncoderEstimator
import com.github.ezauton.core.pathplanning.Path
import com.github.ezauton.core.pathplanning.PathProgressor
import com.github.ezauton.core.pathplanning.purepursuit.LookaheadBounds
import com.github.ezauton.core.pathplanning.purepursuit.PPWaypoint
import com.github.ezauton.core.pathplanning.purepursuit.PurePursuitMovementStrategy
import com.github.ezauton.core.robot.implemented.TankRobotTransLocDrivable
import com.github.ezauton.core.simulation.ActionGroup
import com.github.ezauton.core.simulation.SimulatedTankRobot
import com.github.ezauton.core.utils.RealClock
import com.github.ezauton.recorder.Recording
import com.github.ezauton.recorder.base.PurePursuitRecorder
import com.github.ezauton.recorder.base.RobotStateRecorder
import com.github.ezauton.recorder.base.TankDriveableRecorder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.IOException
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
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

    val waypoint1 = PPWaypoint.simple2D(0.m, 0.m, 0.0.mps, 3.0.mpss, -4.0.mpss)
    val waypoint2 = PPWaypoint.simple2D(0.m, 6.m ,5.0.mps, 3.0.mpss, -4.0.mpss)
    val waypoint3 = PPWaypoint.simple2D(0.m, 20.m, 0.0.mps, 3.0.mpss, -4.0.mpss)

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
    val waypoint1 = PPWaypoint.simple2D(0.m 0.m, 0.mps, 3.mpss, -3.0.mpss)
    val waypoint2 = PPWaypoint.simple2D(6.m 6.m, 5.mps, 3.mpss, -3.0.mpss)
    val waypoint3 = PPWaypoint.simple2D(12.m, 0.m, 0.mps, 3.mpss, -3.0.mpss)

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
  private fun test(name: String, path: Path<Distance>) {

    val progressor = PathProgressor(path)

    val ppMoveStrat = PurePursuitMovementStrategy(progressor, 0.001.m)

    // Might be a problem
    val simulatedRobot = SimulatedTankRobot(1.m, RealClock, 14.0.mpss, 0.3.mps, 16.0.mps)
    simulatedRobot.defaultLocEstimator.reset()
    val leftMotor = simulatedRobot.leftMotor
    val rightMotor = simulatedRobot.rightMotor

    val locEstimator = TankRobotEncoderEncoderEstimator(simulatedRobot.leftDistanceSensor, simulatedRobot.rightDistanceSensor, simulatedRobot)
    locEstimator.reset()

    val lookahead = LookaheadBounds(1.0.m, 5.0.m, 2.0.mps, 10.0.mps, locEstimator)

    val tankRobotTransLocDriveable = TankRobotTransLocDrivable(leftMotor, rightMotor, locEstimator, locEstimator, simulatedRobot)

//    val rec = Recording()
//    rec.addSubRecording(PurePursuitRecorder(RealClock, path, ppMoveStrat))
//    rec.addSubRecording(RobotStateRecorder(RealClock, locEstimator, locEstimator, 30 / 12.0, 2.0))
//    rec.addSubRecording(TankDriveableRecorder("td", RealClock, simulatedRobot.defaultTransLocDriveable))

    val purePursuitAction = purePursuit(20.ms, {0.0.mps}, ppMoveStrat, locEstimator, lookahead, tankRobotTransLocDriveable)

    val updateKinematics = BackgroundAction(2, TimeUnit.MILLISECONDS, Runnable { simulatedRobot.update() })

    val recording = Recording()
      .addSubRecording(RobotStateRecorder("robotstate", simulation.clock, locEstimator, locEstimator, simulatedRobot.lateralWheelDistance, 1.5))
      .addSubRecording(PurePursuitRecorder("pp", simulation.clock, path, ppMoveStrat))
      .addSubRecording(TankDriveableRecorder("td", simulation.clock, tankRobotTransLocDriveable))

    val updateRecording = BackgroundAction(20, TimeUnit.MILLISECONDS, Runnable { recording.update() })

    // Used to update the velocities of left and right motors while also updating the calculations for the location of the robot
    val backgroundAction = BackgroundAction(20, TimeUnit.MILLISECONDS, Runnable { locEstimator.update() }, Runnable { rec.update() })

    val group = ActionGroup()
      .with(updateKinematics)
      .with(backgroundAction)
      .with(updateRecording)
      .addSequential(purePursuitAction)
    simulation.add(group)

    // run the simulator for 30 seconds
    try {
      simulation.runSimulation(30, TimeUnit.SECONDS)
    } finally {
      try {
        recording.save("$name.json")
      } catch (e: IOException) {
        e.printStackTrace()
      }
    }

    val leftWheelVelocity = locEstimator.leftTranslationalWheelVelocity
    assertEquals(0.0, leftWheelVelocity, 0.5, "left wheel velocity")

    val rightWheelVelocity = locEstimator.rightTranslationalWheelVelocity
    assertEquals(0.0, rightWheelVelocity, 0.5, "right wheel velocity")

    // The final location after the simulator
    val finalLoc = locEstimator.estimateLocation()

    // If the final loc is approximately equal to the last waypoint
    approxEqual(path.end, finalLoc, 0.2)

    // If the final loc is approximately equal to the last waypoint
    approxEqual(path.end, finalLoc, 0.2)
  }

  /**
   * Test the path with a robot max acceleration 14ft/s^2, min velocity 0.3ft/s, maxVelocity 16ft/s
   *
   * @param waypoints
   */
  @Throws(TimeoutException::class, ExecutionException::class)
  private fun test(name: String, vararg waypoints: PPWaypoint) {
    val pathGenerator = PP_PathGenerator(*waypoints)
    val path = pathGenerator.generate(0.05)
    test(name, path)
  }

  private fun approxEqual(a: ScalarVector, b: ScalarVector, epsilon: Double) {
    val bElements = b.elements
    val aElements = a.elements
    for (i in aElements.indices) {
      assertEquals(aElements[i], bElements[i], epsilon)
    }
  }

  companion object {

    private val LATERAL_WHEEL_DIST = 4.0
  }
}
