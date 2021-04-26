package com.github.ezauton.core.action

import com.github.ezauton.conversion.Time
import com.github.ezauton.conversion.mps
import com.github.ezauton.core.localization.TranslationalLocationEstimator
import com.github.ezauton.core.pathplanning.purepursuit.Lookahead
import com.github.ezauton.core.pathplanning.purepursuit.PurePursuitMovementStrategy
import com.github.ezauton.core.robot.subsystems.TranslationalLocationDrivable

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
  purePursuitMovementStrategy: PurePursuitMovementStrategy,
  translationalLocationEstimator: TranslationalLocationEstimator,
  lookahead: Lookahead,
  translationalLocationDrivable: TranslationalLocationDrivable
) = action {
  periodic { loop ->

    if (purePursuitMovementStrategy.isFinished) {
      translationalLocationDrivable.driveSpeed(0.0.mps)
      loop.stop()
    }

    val loc = translationalLocationEstimator.estimateLocation()
    val goalPoint = purePursuitMovementStrategy.update(loc, lookahead.lookahead)
    val path = purePursuitMovementStrategy.path
    val current = path.current
    val closestPoint = current.getClosestPoint(loc)
    val absoluteDistanceUsed = current.getAbsoluteDistance(closestPoint)
    val speedUsed = current.getSpeed(absoluteDistanceUsed)
    translationalLocationDrivable.driveTowardTransLoc(speedUsed, goalPoint)
  }
}
