package com.github.ezauton.core.pathplanning

import com.github.ezauton.conversion.*
import com.github.ezauton.core.utils.math.quadratic
import java.util.*

/**
 * Contains the pose of the robot at a certain time and distance. This class provides useful tools
 * for extrapolating future/previous MotionStates based on distances/times.
 */
// TODO: Make a subclass for the purposes of PP Logging, ala RC2018:PurePursuitFrame
class MotionState(val position: Distance, val speed: LinearVelocity, val acceleration: LinearAcceleration, val time: Time) {

  /**
   * @param time
   * @return The future Motion State given a time
   */
  fun extrapolateTime(time: Time): MotionState {
    val dt = time - this.time
    val siUnit = (speed * dt)

    val b = (((1 / 2.0) * acceleration * dt) * dt)

    return MotionState(
      position + siUnit + b,
      speed + acceleration * dt, acceleration, time
    )
  }

  /**
   * Return a copy of this object, but with a different acceleration value
   *
   * @param a The new acceleration value
   * @return This, but with the different accel value
   */
  fun forAcceleration(a: LinearAcceleration): MotionState {
    return MotionState(position, speed, a, time)
  }

  /**
   * @param pos
   * @return The future Motion State given a pos
   */
  fun extrapolatePos(pos: Distance): MotionState {
    return extrapolateTime(timeByPos(pos)) // TODO: This right
  }

  /**
   * @param position
   * @return The time it will be given a position by extrapolation
   */
  fun timeByPos(position: Distance): Time {
    val solutions = quadratic(a = 1 / 2.0 * acceleration.s, b = speed.s, c = (this.position - position).s).toMutableSet()
    solutions.removeIf { it < 0 }

    return if (solutions.isEmpty()) {
      invalid()
    } else SI(Collections.min(solutions) + time.value) // TODO: does this work
  }
}
