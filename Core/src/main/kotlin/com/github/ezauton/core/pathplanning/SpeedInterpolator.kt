package com.github.ezauton.core.pathplanning

import com.github.ezauton.conversion.*
import com.github.ezauton.core.utils.LinearInterpolationMap

/**
 * A fully-implemented linear path segment. This class
 * relies on finding motion states every dt and from this
 * using an interpolation map to see what desired motion states
 * should be for certain distances.
 *
 * @param speedStart Target speed to go at the start of the path
 * @param speedStop Target speed to go at the end of the path
 * @param dt The difference in time should be extrapolated
 */
class SpeedInterpolator(
  private val length: Distance,
  private val speedStart: LinearVelocity,
  private val speedStop: LinearVelocity,
  private val dt: Time,
  private val maxAccel: LinearAcceleration,
  private val maxDecel: LinearAcceleration
) {

  private lateinit var speedInterpolator: LinearInterpolationMap

  init {
    extrap()
  }

  /**
   * Build this.speedInterpolator
   */
  private fun extrap() {
    // You have probably seen: d_f = 1/2at^2 + vt + d_i
    // However, we are not having constant acceleration... so we need

    // Make extrapolation for speed
    speedInterpolator = LinearInterpolationMap.from(mapOf(0.0 to speedStart.value))

    // Use kinematics equations built into the MotionState class to build speedInterpolator
    if (speedStart < speedStop) {     // accel
      var motionState = MotionState(0.0.meters, speedStart, maxAccel, 0.seconds)
      while (motionState.speed < speedStop) {
        motionState = motionState.extrapolateTime(motionState.time + dt)
        val position = motionState.position
        if (position > length) {
          val velLeft = speedStop - motionState.speed
          assert(velLeft.isNegative || velLeft.isZero)
        }
        speedInterpolator[position.value] = min(speedStop, motionState.speed).value
      }
    } else if (speedStart > speedStop) { // decel
      var motionState = MotionState(length, speedStop, maxDecel, 0.0.seconds)
      speedInterpolator[length.value] = speedStop.value
      while (motionState.speed < speedStart) {
        motionState = motionState.extrapolateTime(motionState.time - dt)
        val position = motionState.position
        if (position.isNegative) {
          val velLeft = speedStart - motionState.speed
          require(!velLeft.isNegative)
        }
        speedInterpolator[position.value] = min(speedStart, motionState.speed).value
      }
    }
  }

  operator fun get(relativeDistance: Distance): LinearVelocity {
    return speedInterpolator[relativeDistance.value].withUnit()
  }
}
