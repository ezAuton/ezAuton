package com.github.ezauton.core.action

import com.github.ezauton.core.Duration
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
 * @param period                         How often to update estimated position, robot control, etc
 * @param timeUnit                       The timeunit that period is in
 * @param purePursuitMovementStrategy    Our movement strategy.
 * @param translationalLocationEstimator An object that knows where we are on a 2D plane
 * @param lookahead                      An instance of [Lookahead] that can tell us how far along the path to look ahead
 * @param translationalLocationDrivable The drivetrain of the robot
 */
(period: Duration,
 private val purePursuitMovementStrategy: PurePursuitMovementStrategy,
 private val translationalLocationEstimator: TranslationalLocationEstimator,
 private val lookahead: Lookahead,
 private val translationalLocationDrivable: TranslationalLocationDrivable) : PeriodicAction(period) {
    /**
     * @return The most reset speed used by Pure Pursuit
     */
    var speedUsed: Double = 0.toDouble()
        private set
    /**
     * @return The most reset absolute distance used by Pure Pursuit
     */
    var absoluteDistanceUsed: Double = 0.toDouble()
        private set

    override fun execute() {
        // Find out where to drive to
        val loc = translationalLocationEstimator.estimateLocation()
        val goalPoint = purePursuitMovementStrategy.update(loc, lookahead.lookahead)

        val path = purePursuitMovementStrategy.path
        val current = path.current
        val closestPoint = current.getClosestPoint(loc)
        absoluteDistanceUsed = current.getAbsoluteDistance(closestPoint)
        speedUsed = current.getSpeed(absoluteDistanceUsed)
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
