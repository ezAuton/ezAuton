package com.github.ezauton.core.pathplanning

import com.github.ezauton.conversion.*
import com.github.ezauton.core.utils.math.quadratic
import java.util.Collections

/**
 * Contains the pose of the robot at a certain time and distance. This class provides useful tools
 * for extrapolating future/previous MotionStates based on distances/times.
 */
// TODO: Make a subclass for the purposes of PP Logging, ala RC2018:PurePursuitFrame
class MotionState(val position: SIUnit<Distance>, val speed: SIUnit<Velocity>, val acceleration: SIUnit<Acceleration>, val time: SIUnit<Time>) {

    /**
     * @param time
     * @return The future Motion State given a time
     */
    fun extrapolateTime(time: SIUnit<Time>): MotionState {
        val dt = time - this.time
        val siUnit = (speed * dt)

        val b = (((1 / 2.0) * acceleration * dt).s()*dt).s()

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
        val solutions = quadratic(a = 1 / 2.0 * acceleration, b = speed, c = this.position - position)
        solutions.removeIf { it < 0 }
        return if (solutions.isEmpty()) {
            Double.NaN
        } else Collections.min(solutions) + time
    }
}
