package com.github.ezauton.core.action

import com.github.ezauton.conversion.Duration
import com.github.ezauton.core.localization.TranslationalLocationEstimator
import com.github.ezauton.core.pathplanning.purepursuit.Lookahead
import com.github.ezauton.core.pathplanning.purepursuit.PurePursuitMovementStrategy
import com.github.ezauton.core.robot.subsystems.TranslationalLocationDrivable

/**
 * A Pure Pursuit action which can be used in simulation or as a WPILib Command
 */
class PurePursuitAction
/**
 * Create a PP Command
 *
 * @param period How often to update estimated position, robot control, etc
 * @param purePursuitMovementStrategy Our movement strategy.
 * @param translationalLocationEstimator An object that knows where we are on a 2D plane
 * @param lookahead An instance of [Lookahead] that can tell us how far along the path to look ahead
 * @param translationalLocationDrivable The drivetrain of the robot
 */(
        period: Duration,
        private val purePursuitMovementStrategy: PurePursuitMovementStrategy,
        private val translationalLocationEstimator: TranslationalLocationEstimator,
        private val lookahead: Lookahead,
        private val translationalLocationDrivable: TranslationalLocationDrivable
) : PeriodicAction(period) {

    override fun execute() {
        // Find out where to drive to
        val loc = translationalLocationEstimator.estimateLocation()
        val goalPoint = purePursuitMovementStrategy.update(loc, lookahead.lookahead)

        val path = purePursuitMovementStrategy.path
        val current = path.current
        val closestPoint = current.getClosestPoint(loc)
        val absoluteDistanceUsed = current.getAbsoluteDistance(closestPoint)
        val speedUsed = current.getSpeed(absoluteDistanceUsed)
        translationalLocationDrivable.driveTowardTransLoc(speedUsed, goalPoint)
    }

    override fun isFinished(): Boolean {
        if (purePursuitMovementStrategy.isFinished) {
            translationalLocationDrivable.driveSpeed(0.0)
            return true
        }
        return false
    }
}