package com.github.ezauton.core.pathplanning

import com.github.ezauton.core.utils.MathUtils

import java.util.Collections

/**
 * Contains the pose of the robot at a certain time and distance. This class provides useful tools
 * for extrapolating future/previous MotionStates based on distances/times.
 */
//TODO: Make a subclass for the purposes of PP Logging, ala RC2018:PurePursuitFrame
class MotionState(val position: Double, val speed: Double, val acceleration: Double, val time: Double) {

    /**
     * @param time
     * @return The future Motion State given a time
     */
    fun extrapolateTime(time: Double): MotionState {
        val dt = time - this.time
        return MotionState(position + speed * dt + 1 / 2.0 * acceleration * dt * dt,
                speed + acceleration * dt, acceleration, time)
    }

    /**
     * Return a copy of this object, but with a different acceleration value
     *
     * @param a The new acceleration value
     * @return This, but with the different accel value
     */
    fun forAcceleration(a: Double): MotionState {
        return MotionState(position, speed, a, time)
    }

    /**
     * @param pos
     * @return The future Motion State given a pos
     */
    fun extrapolatePos(pos: Double): MotionState {
        return extrapolateTime(timeByPos(position))
    }

    /**
     * @param position
     * @return The time it will be given a position by extrapolation
     */
    fun timeByPos(position: Double): Double {
        val solutions = MathUtils.Algebra.quadratic(1 / 2.0 * acceleration, speed, this.position - position)
        solutions.removeIf { `val` -> `val` < 0 }
        return if (solutions.size == 0) {
            java.lang.Double.NaN
        } else Collections.min(solutions) + time
    }

}
