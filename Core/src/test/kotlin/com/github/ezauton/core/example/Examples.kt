package com.github.ezauton.core.example

import com.github.ezauton.conversion.*
import com.github.ezauton.core.action.action
import com.github.ezauton.core.action.purePursuit
import com.github.ezauton.core.pathplanning.purepursuit.ScalingLookahead
import com.github.ezauton.core.pathplanning.purepursuit.trajectory
import com.github.ezauton.core.record.recording
import com.github.ezauton.core.record.recordingFlow
import com.github.ezauton.core.record.save
import com.github.ezauton.core.simulation.SimulatedTankRobot
import com.github.ezauton.core.simulation.parallel
import kotlinx.coroutines.flow.collect

suspend fun run() {

  // (1) a straight line trajectory starting at (0,0) going to (0,20) with a max speed of 3 m/s.
  val trajectory = trajectory(samplePeriod = 5.ms) {
    point(0.m, 0.m, speed = 0.mps, acceleration = 13.0.mps / s, deceleration = 12.0.mps / s)
    point(0.m, 10.m, speed = 3.mps, acceleration = 13.0.mps / s, deceleration = 12.0.mps / s)
    point(0.m, 20.m, speed = 0.mps, acceleration = 13.0.mps / s, deceleration = 12.0.mps / s)
  }

  // (2) a simulated robot
  val robot = SimulatedTankRobot.create(lateralWheelDistance = 1.m, maxAccel = 14.0.mpss, minVel = 0.3.mps, maxVel = 16.0.mps)

  // (3) a lookahead that scales with the velocity of the robot
  val lookahead = ScalingLookahead(distanceRange = 1.0.m..5.0.m, speedRange = 2.0.mps..10.0.mps, velocityEstimator = robot)

  // (4) pure pursuit
  val purePursuit = robot.purePursuit(period = 10.ms, trajectory, lookahead)

  // (4) the action we will actually be running
  val action = action {

    // (5) record everything inside this
    val recording = recording {

      // (6) include data about the path
      include(trajectory.path.simpleRepr)

      // (7) run pure pursuit in parallel
      parallel(purePursuit)

      // (8) every 10ms sample data from the robot (like location) and include in recording
      sample(10.ms, robot)
    }

    // (9) save the recording to ~/.ezauton/test.json
    recording.save("test.json")
  }

  action.run()
}




suspend fun runFlow() {

  // (1) a straight line trajectory starting at (0,0) going to (0,20) with a max speed of 3 m/s.
  val trajectory = trajectory(samplePeriod = 5.ms) {
    point(0.m, 0.m, speed = 0.mps, acceleration = 13.0.mps / s, deceleration = 12.0.mps / s)
    point(0.m, 10.m, speed = 3.mps, acceleration = 13.0.mps / s, deceleration = 12.0.mps / s)
    point(0.m, 20.m, speed = 0.mps, acceleration = 13.0.mps / s, deceleration = 12.0.mps / s)
  }

  // (2) a simulated robot
  val robot = SimulatedTankRobot.create(lateralWheelDistance = 1.m, maxAccel = 14.0.mpss, minVel = 0.3.mps, maxVel = 16.0.mps)

  // (3) a lookahead that scales with the velocity of the robot
  val lookahead = ScalingLookahead(distanceRange = 1.0.m..5.0.m, speedRange = 2.0.mps..10.0.mps, velocityEstimator = robot)

  // (4) pure pursuit
  val purePursuit = robot.purePursuit(period = 10.ms, trajectory, lookahead)

  val action = action {
    val flow = recordingFlow {
      include(trajectory.path.simpleRepr)
      parallel(purePursuit)
      sample(10.ms, robot)
    }

    flow.collect { packet ->
      println(packet.toJson()) // we could send this over network tables
    }

  }

  action.run()

}


suspend fun main() {
  runFlow()
  run()
}
