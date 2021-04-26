package com.github.ezauton.core.action

import com.github.ezauton.conversion.Distance
import com.github.ezauton.conversion.LinearVelocity
import com.github.ezauton.conversion.Time
import com.github.ezauton.conversion.mps
import com.github.ezauton.core.localization.TranslationalLocationEstimator
import com.github.ezauton.core.pathplanning.purepursuit.Lookahead
import com.github.ezauton.core.pathplanning.purepursuit.PurePursuitMovementStrategy
import com.github.ezauton.core.pathplanning.purepursuit.Update
import com.github.ezauton.core.robot.subsystems.TranslationalLocationDrivable


typealias Speed = (distance: Distance) -> LinearVelocity

/**
 * A Pure Pursuit action which can be used in simulation or as a WPILib Command
 *
 * @param period How often to update estimated position, robot control, etc
 * @param purePursuitMovementStrategy Our movement strategy.
 * @param translationalLocationEstimator An object that knows where we are on a 2D plane
 * @param lookahead An instance of [Lookahead] that can tell us how far along the path to look ahead
 * @param translationalLocationDrivable The drivetrain of the robot
 */
fun purePursuit(
  period: Time,
  speedFunction: Speed,
  purePursuitMovementStrategy: PurePursuitMovementStrategy,
  translationalLocationEstimator: TranslationalLocationEstimator,
  lookahead: Lookahead,
  translationalLocationDrivable: TranslationalLocationDrivable
) = action {
  periodic(period) { loop ->

    val currentLocation = translationalLocationEstimator.estimateLocation()

    when (val update = purePursuitMovementStrategy.update(currentLocation, lookahead.lookahead)) {
      is Update.Finished -> {
        translationalLocationDrivable.driveSpeed(0.0.mps)
        loop.stop()
      }
      is Update.Result -> {
        val speedUsed = speedFunction(update.on.distance)
        translationalLocationDrivable.driveTowardTransLoc(speedUsed, update.goal)
      }
    }


  }
}
