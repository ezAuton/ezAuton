package com.github.ezauton.core.pathplanning.purepursuit

import com.github.ezauton.conversion.Distance
import com.github.ezauton.conversion.LinearVelocity
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
  (private val minDistance: Distance, private val maxDistance: Distance, private val minSpeed: LinearVelocity, maxSpeed: LinearVelocity, private val velocityEstimator: VelocityEstimator) : Lookahead {

  private val dDistance = maxDistance - minDistance
  private val dSpeed = maxSpeed - minSpeed

  /**
   * Based on the current speed as described by this.velocityEstimator, calculate a lookahead
   *
   * @return The lookahead to use
   */
  override val lookahead: Distance
    get() {
      val speed = velocityEstimator.translationalVelocity.abs()
      val lookahead = dDistance * ((speed - minSpeed) / dSpeed) + minDistance
      return lookahead.coerceIn(minDistance..maxDistance)
    }
}
