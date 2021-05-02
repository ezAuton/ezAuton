package com.github.ezauton.core.action

import com.github.ezauton.conversion.*
import com.github.ezauton.core.localization.TranslationalLocationEstimator
import com.github.ezauton.core.pathplanning.PathProgressor
import com.github.ezauton.core.pathplanning.Trajectory
import com.github.ezauton.core.pathplanning.purepursuit.*
import com.github.ezauton.core.robot.subsystems.TranslationalLocationDrivable
import kotlinx.coroutines.channels.Channel


typealias Speed = (distance: Distance) -> LinearVelocity

/**
 * A Pure Pursuit action which can be used in simulation or as a WPILib Command
 *
 * @param period How often to update estimated position, robot control, etc
 * @param translationalLocationEstimator An object that knows where we are on a 2D plane
 * @param lookahead An instance of [Lookahead] that can tell us how far along the path to look ahead
 * @param translationalLocationDrivable The drivetrain of the robot
 */
fun purePursuit(
  period: Time,
  trajectory: Trajectory,
  translationalLocationEstimator: TranslationalLocationEstimator,
  translationalLocationDrivable: TranslationalLocationDrivable,
  lookahead: Lookahead,
  stopDistance: Distance = 0.001.m,
  dataChannel: Channel<PurePursuitData>? = null
) = action {
  val progressor = PathProgressor(trajectory.path)
  val speedFunction = trajectory.speed
  val ppMoveStrat = PurePursuitMovementStrategy(progressor, stopDistance, dataChannel)
  periodic(period) { loop ->

    val currentLocation = translationalLocationEstimator.estimateLocation()

    when (val update = ppMoveStrat.update(currentLocation, lookahead.lookahead)) {
      is Update.Finished -> {
        translationalLocationDrivable.driveSpeed(0.0.mps)
        loop.stop()
      }
      is Update.Result -> {
        val speedUsed = speedFunction(update.on.distance)
//        println("speedUsed $speedUsed")
        translationalLocationDrivable.driveTowardTransLoc(speedUsed, update.goal)
      }
    }

  }
  dataChannel?.close()
}
