package com.github.ezauton.core.pathplanning.purepursuit

import com.github.ezauton.conversion.*
import com.github.ezauton.core.localization.sensors.VelocityEstimator

/**
 * Easy lookahead implementation given a speed. Takes in min speed, max speed, min lookahead, max lookahead and
 * performs a linear interpolation.
 */
class LookaheadBounds
/**
 * Create some lookahead bounds
 *
 * @param minDistance Minimum lookahead
 * @param maxDistance Maximum lookahead
 * @param minSpeed Minimum speed where lookahead is allowed to be dynamic
 * @param maxSpeed Maximum speed where lookahead is allowed to be dynamic
 * @param velocityEstimator Estimator of the robot's velocity. Used to calculate lookahead based on current speed.
 */
(private val minDistance: SIUnit<Distance>, private val maxDistance: SIUnit<Distance>, private val minSpeed: SIUnit<Velocity>, maxSpeed: SIUnit<Velocity>, private val velocityEstimator: VelocityEstimator) : Lookahead {

    private val dDistance = maxDistance - minDistance
    private val dSpeed = maxSpeed - minSpeed

    /**
     * Based on the current speed as described by this.velocityEstimator, calculate a lookahead
     *
     * @return The lookahead to use
     */
    override val lookahead: SIUnit<Distance>
        get() {
            val speed = abs(velocityEstimator.translationalVelocity)
            val lookahead = dDistance * ((speed - minSpeed) / dSpeed) + minDistance
            return lookahead.coerceIn(minDistance .. maxDistance)
        }
}
