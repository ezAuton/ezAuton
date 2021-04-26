//package com.github.ezauton.core.purepursuit
//
//import com.github.ezauton.conversion.ScalarVector
//import com.github.ezauton.core.action.ActionGroup
//import com.github.ezauton.core.action.PurePursuitAction
//import com.github.ezauton.core.helper.PathHelper
//import com.github.ezauton.core.localization.estimators.TankRobotEncoderEncoderEstimator
//import com.github.ezauton.core.pathplanning.PP_PathGenerator
//import com.github.ezauton.core.pathplanning.Path
//import com.github.ezauton.core.pathplanning.purepursuit.LookaheadBounds
//import com.github.ezauton.core.pathplanning.purepursuit.PPWaypoint
//import com.github.ezauton.core.pathplanning.purepursuit.PurePursuitMovementStrategy
//import com.github.ezauton.core.pathplanning.purepursuit.SplinePPWaypoint
//import com.github.ezauton.core.robot.implemented.TankRobotTransLocDrivable
//import com.github.ezauton.core.simulation.SimulatedTankRobot
//import com.github.ezauton.core.simulation.TimeWarpedSimulation
//import com.github.ezauton.recorder.Recording
//import com.github.ezauton.recorder.base.PurePursuitRecorder
//import com.github.ezauton.recorder.base.RobotStateRecorder
//import com.github.ezauton.recorder.base.TankDriveableRecorder
//import org.junit.jupiter.api.Assertions.assertEquals
//import org.junit.jupiter.api.Test
//import java.io.IOException
//import java.util.concurrent.ExecutionException
//import java.util.concurrent.TimeUnit
//import java.util.concurrent.TimeoutException
//
//class PPSimulatorTest {
//
//  //    @Test
//  @Throws(TimeoutException::class, ExecutionException::class)
//  fun testLeftToRightScale() {
//    val build = PPWaypoint.Builder()
//      .add(0.0, 0.0, 16.0, 13.0, -12.0)
//      .add(0.0, 4.0, 16.0, 13.0, -12.0)
//      .add(-0.5, 8.589, 16.0, 13.0, -12.0)
//      .add(-0.5, 12.405, 13.0, 13.0, -12.0)
//      .add(-0.5, 17.0, 8.5, 13.0, -12.0)
//      .add(1.5, 19.4, 0.0, 13.0, -12.0)
//      .buildArray()
//
//    test("testLeftToRightScale", *build)
//  }
//
//  @Test
//  @Throws(TimeoutException::class, ExecutionException::class)
//  fun testStraight() {
//
//    val waypoint1 = PPWaypoint.simple2D(0.0, 0.0, 0.0, 3.0, -4.0)
//    val waypoint2 = PPWaypoint.simple2D(0.0, 6.0, 5.0, 3.0, -4.0)
//    val waypoint3 = PPWaypoint.simple2D(0.0, 20.0, 0.0, 3.0, -4.0)
//
//    test("testStraight", waypoint1, waypoint2, waypoint3)
//  }
//
//  @Test
//  @Throws(TimeoutException::class, ExecutionException::class)
//  fun testStraightGeneric() {
//    test("testStraightGeneric", PathHelper.STRAIGHT_12UNITS)
//  }
//
//  @Test
//  @Throws(TimeoutException::class, ExecutionException::class)
//  fun testRight() {
//    val waypoint1 = PPWaypoint.simple2D(0.0, 0.0, 0.0, 3.0, -3.0)
//    val waypoint2 = PPWaypoint.simple2D(6.0, 6.0, 5.0, 3.0, -3.0)
//    val waypoint3 = PPWaypoint.simple2D(12.0, 0.0, 0.0, 3.0, -3.0)
//
//    test("testRight", waypoint1, waypoint2, waypoint3)
//  }
//
//  @Test
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
//
//  @Throws(TimeoutException::class, ExecutionException::class)
//  private fun test(name: String, path: Path) {
//
//    val ppMoveStrat = PurePursuitMovementStrategy(path, 0.001)
//
//    // Not a problem
//    val simulation = TimeWarpedSimulation(1.0)
//
//    // Might be a problem
//    val simulatedRobot = SimulatedTankRobot(LATERAL_WHEEL_DIST, simulation.clock, 14.0, 0.3, 16.0)
//    simulatedRobot.defaultLocEstimator.reset()
//    val leftMotor = simulatedRobot.leftMotor
//    val rightMotor = simulatedRobot.rightMotor
//
//    val locEstimator = TankRobotEncoderEncoderEstimator(simulatedRobot.leftDistanceSensor, simulatedRobot.rightDistanceSensor, simulatedRobot)
//    locEstimator.reset()
//
//    val lookahead = LookaheadBounds(1.0, 5.0, 2.0, 10.0, locEstimator)
//
//    val tankRobotTransLocDriveable = TankRobotTransLocDrivable(leftMotor, rightMotor, locEstimator, locEstimator, simulatedRobot)
//
//    val rec = Recording()
//    rec.addSubRecording(PurePursuitRecorder(simulation.clock, path, ppMoveStrat))
//    rec.addSubRecording(RobotStateRecorder(simulation.clock, locEstimator, locEstimator, 30 / 12.0, 2.0))
//    rec.addSubRecording(TankDriveableRecorder("td", simulation.clock, simulatedRobot.defaultTransLocDriveable))
//
//    val purePursuitAction = PurePursuitAction(20, TimeUnit.MILLISECONDS, ppMoveStrat, locEstimator, lookahead, tankRobotTransLocDriveable)
//
//    val updateKinematics = BackgroundAction(2, TimeUnit.MILLISECONDS, Runnable { simulatedRobot.update() })
//
//    val recording = Recording()
//      .addSubRecording(RobotStateRecorder("robotstate", simulation.clock, locEstimator, locEstimator, simulatedRobot.lateralWheelDistance, 1.5))
//      .addSubRecording(PurePursuitRecorder("pp", simulation.clock, path, ppMoveStrat))
//      .addSubRecording(TankDriveableRecorder("td", simulation.clock, tankRobotTransLocDriveable))
//
//    val updateRecording = BackgroundAction(20, TimeUnit.MILLISECONDS, Runnable { recording.update() })
//
//    // Used to update the velocities of left and right motors while also updating the calculations for the location of the robot
//    val backgroundAction = BackgroundAction(20, TimeUnit.MILLISECONDS, Runnable { locEstimator.update() }, Runnable { rec.update() })
//
//    val group = ActionGroup()
//      .with(updateKinematics)
//      .with(backgroundAction)
//      .with(updateRecording)
//      .addSequential(purePursuitAction)
//    simulation.add(group)
//
//    // run the simulator for 30 seconds
//    try {
//      simulation.runSimulation(30, TimeUnit.SECONDS)
//    } finally {
//      try {
//        recording.save("$name.json")
//      } catch (e: IOException) {
//        e.printStackTrace()
//      }
//    }
//
//    val leftWheelVelocity = locEstimator.leftTranslationalWheelVelocity
//    assertEquals(0.0, leftWheelVelocity, 0.5, "left wheel velocity")
//
//    val rightWheelVelocity = locEstimator.rightTranslationalWheelVelocity
//    assertEquals(0.0, rightWheelVelocity, 0.5, "right wheel velocity")
//
//    // The final location after the simulator
//    val finalLoc = locEstimator.estimateLocation()
//
//    // If the final loc is approximately equal to the last waypoint
//    approxEqual(path.end, finalLoc, 0.2)
//
//    // If the final loc is approximately equal to the last waypoint
//    approxEqual(path.end, finalLoc, 0.2)
//  }
//
//  /**
//   * Test the path with a robot max acceleration 14ft/s^2, min velocity 0.3ft/s, maxVelocity 16ft/s
//   *
//   * @param waypoints
//   */
//  @Throws(TimeoutException::class, ExecutionException::class)
//  private fun test(name: String, vararg waypoints: PPWaypoint) {
//    val pathGenerator = PP_PathGenerator(*waypoints)
//    val path = pathGenerator.generate(0.05)
//    test(name, path)
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
//  companion object {
//
//    private val LATERAL_WHEEL_DIST = 4.0
//  }
//}
