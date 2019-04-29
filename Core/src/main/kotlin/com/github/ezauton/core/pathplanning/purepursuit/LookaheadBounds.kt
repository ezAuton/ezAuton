package com.github.ezauton.core.pathplanning.purepursuit

import com.github.ezauton.core.localization.sensors.VelocityEstimator

/**
 * Easy lookahead implementation given a speed. Takes in min speed, max speed, min lookahead, max lookahead and
 * performs a linear interpolation.
 */
class LookaheadBounds
/**
 * Create some lookahead bounds
 *
 * @param minDistance       Minimum lookahead
 * @param maxDistance       Maximum lookahead
 * @param minSpeed          Minimum speed where lookahead is allowed to be dynamic
 * @param maxSpeed          Maximum speed where lookahead is allowed to be dynamic
 * @param velocityEstimator Estimator of the robot's velocity. Used to calculate lookahead based on current speed.
 */
(private val minDistance: Double, private val maxDistance: Double, private val minSpeed: Double, maxSpeed: Double, private val velocityEstimator: VelocityEstimator) : Lookahead {

    private val dDistance: Double
    private val dSpeed: Double

    /**
     * Based on the current spead as described by this.velocityEstimator, calculate a lookahead
     *
     * @return The lookahead to use
     */
    override val lookahead: Double
        get() {
            val speed = Math.abs(velocityEstimator.translationalVelocity)
            val lookahead = dDistance * (speed - minSpeed) / dSpeed + minDistance
            return if (java.lang.Double.isNaN(lookahead)) minDistance else Math.max(minDistance, Math.min(maxDistance, lookahead))
        }

    init {
        dDistance = maxDistance - minDistance
        dSpeed = maxSpeed - minSpeed
    }
}
