package com.github.ezauton.core.pathplanning

import com.github.ezauton.conversion.ScalarVector
import com.github.ezauton.core.utils.LinearInterpolationMap

/**
 * A fully-implemented linear path segment. This class
 * relies on finding motion states every dt and from this
 * using an interpolation map to see what desired motion states
 * should be for certain distances.
 *
 * @param from Starting location of the path segment
 * @param to Ending location of the path segment
 * @param finish If this is the last path segment
 * @param beginning If this is the first path segment
 * @param distanceStart Distance along the path from the beginning to `from`
 * @param speedStart Target speed to go at the start of the path
 * @param speedStop Target speed to go at the end of the path
 * @param dt The difference in time should be extrapolated
 */
class PathSegmentInterpolated(
        from: ScalarVector,
        to: ScalarVector,
        finish: Boolean,
        beginning: Boolean,
        distanceStart: Double,
        val speedStart: Double,
        val speedStop: Double,
        val dt: Double,
        val maxAccel: Double,
        val maxDecel: Double
) : LinearPathSegment(from, to, finish, beginning, distanceStart) {

    private lateinit var speedInterpolator: LinearInterpolationMap

    /**
     * Build this.speedInterpolator
     */
    private fun extrap() {
        // You have probably seen: d_f = 1/2at^2 + vt + d_i
        // However, we are not having constant acceleration... so we need

        // Make extrapolation for speed
        speedInterpolator = LinearInterpolationMap.from(mapOf(0.0 to speedStart))

        // Use kinematics equations built into the MotionState class to build speedInterpolator
        if (speedStart < speedStop)
        // accel
        {
            var motionState = MotionState(0.0, speedStart, maxAccel, 0.0)
            while (motionState.speed < speedStop) {
                motionState = motionState.extrapolateTime(motionState.time + dt)
                val position = motionState.position
                if (position > length) {
                    val velLeft = speedStop - motionState.speed
                    if (velLeft < 0) return
                    val msg = String.format(
                        "Acceleration value too low to execute trajectory from %s To: %s. At max accelerate still needed to accelerate: %.2f",
                        from,
                        to,
                        velLeft
                    )
                    throw IllegalStateException(msg)
                }
                speedInterpolator[position] = Math.min(speedStop, motionState.speed)
            }
        } else if (speedStart > speedStop)
        // decel
        {
            var motionState = MotionState(length, speedStop, maxDecel, 0.0)
            speedInterpolator!![length] = speedStop
            while (motionState.speed < speedStart) {
                motionState = motionState.extrapolateTime(motionState.time - dt)
                val position = motionState.position
                if (position < 0) {
                    val velLeft = speedStart - motionState.speed
                    if (velLeft < 0) return
                    val msg = String.format(
                        "Deceleration (magnitude) value too low to execute trajectory from %s to %s. At max deceleration still needed to decelerate: %.2f",
                        from,
                        to,
                        velLeft
                    )
                    throw IllegalStateException(msg)
                }
                speedInterpolator!![position] = Math.min(speedStart, motionState.speed)
            }
        }
    }

    override fun getSpeed(absoluteDistance: Double): Double {
        val relativeDistance = getRelativeDistance(absoluteDistance)
        return speedInterpolator!!.get(relativeDistance)
    }
}
