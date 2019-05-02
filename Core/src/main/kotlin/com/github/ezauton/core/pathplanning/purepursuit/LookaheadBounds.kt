package com.github.ezauton.core.pathplanning.purepursuit

import com.github.ezauton.conversion.Distance
import com.github.ezauton.conversion.Velocity
import com.github.ezauton.conversion.div
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
(private val minDistance: Distance, private val maxDistance: Distance, private val minSpeed: Velocity, maxSpeed: Velocity, private val velocityEstimator: VelocityEstimator) : Lookahead {

    private val dDistance = maxDistance - minDistance
    private val dSpeed = maxSpeed - minSpeed

    /**
     * Based on the current spead as described by this.velocityEstimator, calculate a lookahead
     *
     * @return The lookahead to use
     */
    override val lookahead: Distance
        get() {
            val speed = velocityEstimator.translationalVelocity.abs()
            val lookahead = dDistance * ((speed - minSpeed) / dSpeed) + minDistance
            return lookahead.coerceIn(minDistance .. maxDistance)
        }
}
