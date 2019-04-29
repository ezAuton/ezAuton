package com.github.ezauton.core.pathplanning.purepursuit


import com.github.ezauton.core.pathplanning.PP_PathGenerator
import com.github.ezauton.core.pathplanning.QuinticSpline
import com.github.ezauton.core.trajectory.geometry.ImmutableVector
import com.github.ezauton.core.utils.MathUtils

import java.io.Serializable
import java.util.ArrayList

/**
 * Waypoint used in Pure Pursuit
 */
class SplinePPWaypoint : PPWaypoint, Serializable {
    val tanVec: ImmutableVector

    /**
     * Create a waypoint for Pure Pursuit to drive to
     *
     * @param location     Where the waypoint is, given that the Y axis is the forward axis
     * @param tanVec       The direction the robot should be pointing in at this waypoint. A larger magnitude means the spline is straighter.
     * @param speed        Approximately how fast the robot should be going by the time it reaches this waypoint
     * @param acceleration Maximum acceleration allowed to reach the target speed
     * @param deceleration Maximum deceleration allowed to reach the target speed
     */
    //TODO: Confirm documentation is accurate
    constructor(location: ImmutableVector, tanVec: ImmutableVector, speed: Double, acceleration: Double, deceleration: Double) : super(location, speed, acceleration, deceleration) {
        this.tanVec = tanVec
    }

    constructor(location: ImmutableVector, theta: Double, speed: Double, acceleration: Double, deceleration: Double) : super(location, speed, acceleration, deceleration) {
        this.tanVec = ImmutableVector(Math.cos(theta) * kTheta, Math.sin(theta) * kTheta)
    }

    override fun toString(): String {
        val sb = StringBuilder("SplinePPWaypoint{")
        sb.append("location=").append(location)
        sb.append(", speed=").append(speed)
        sb.append(", acceleration=").append(acceleration)
        sb.append(", deceleration=").append(deceleration)
        sb.append(", tanVec=").append(tanVec)
        sb.append('}')
        return sb.toString()
    }

    class Builder @JvmOverloads constructor(private val kTheta: Double = SplinePPWaypoint.kTheta) {
        private val waypointList = ArrayList<SplinePPWaypoint>()

        @Deprecated("")
        fun add(x: Double, y: Double, xPrime: Double, yPrime: Double, speed: Double, acceleration: Double, deceleration: Double): Builder {
            val waypoint = SplinePPWaypoint(ImmutableVector(x, y), ImmutableVector(xPrime, yPrime), speed, acceleration, deceleration)
            waypointList.add(waypoint)
            return this
        }

        /**
         * Add a spline waypoint to the builder
         *
         * @param x
         * @param y
         * @param theta        Radians, rotated such that up is 0, left is Math.PI / 2, etc.
         * @param speed
         * @param acceleration
         * @param deceleration
         * @return
         */
        fun add(x: Double, y: Double, theta: Double, speed: Double, acceleration: Double, deceleration: Double): Builder {
            var theta = theta
            theta += Math.PI / 2
            val waypoint = SplinePPWaypoint.simple2D(x, y, theta, speed, acceleration, deceleration)
            waypointList.add(waypoint)
            return this
        }

        fun flipY(): Builder {
            val ret = Builder()
            for (wp in waypointList) {
                ret.add(-wp.location.get(0), wp.location.get(1), -wp.tanVec.get(0), wp.tanVec.get(1), wp.speed, wp.acceleration, wp.deceleration)
            }
            return ret
        }

        fun buildPathGenerator(): PP_PathGenerator {
            return PP_PathGenerator(*QuinticSpline.toPathSegments(buildSplines(), waypointList))
        }

        fun buildSplines(): List<QuinticSpline> {
            if (waypointList.size > 1) {
                val splines = ArrayList<QuinticSpline>()
                for (i in 1 until waypointList.size) {
                    val prevWP = waypointList[i - 1]
                    val thisWP = waypointList[i]
                    var prevWpTanvec = prevWP.tanVec

                    if (MathUtils.epsilonEquals(prevWpTanvec.mag(), kTheta)) {
                        prevWpTanvec = prevWP.tanVec.mul(prevWP.location.dist(thisWP.location))
                    }

                    splines.add(QuinticSpline(prevWP.location, thisWP.location, prevWpTanvec, thisWP.tanVec))
                }
                return splines
            } else {
                throw IllegalStateException("Cannot create spline with less than 2 waypoints")
            }
        }
    }

    companion object {

        private val kTheta = 1.2

        /**
         * A shortcut to making a 2D waypoint
         *
         * @param x            X-coordinate for the location of this waypoint
         * @param y            Y-coordinate for the location of this waypoint
         * @param theta        Angle that the robot should be at when it reaches this waypoint
         * @param speed        Approximately how fast the robot should be going by the time it reaches this waypoint
         * @param acceleration Maximum acceleration allowed to reach the target speed
         * @param deceleration Maximum deceleration allowed to reach the target speed
         * @return A waypoint with the specified properties
         */
        fun simple2D(x: Double, y: Double, theta: Double, speed: Double, acceleration: Double, deceleration: Double): SplinePPWaypoint {
            if (deceleration > 0) {
                throw IllegalArgumentException("Deceleration cannot be positive!")
            }
            return SplinePPWaypoint(ImmutableVector(x, y), theta, speed, acceleration, deceleration)
        }

        /**
         * A shortcut to making a 3D waypoint (Deep Space will have drones so we need 3D PP) ... ofc you never
         * know when we will need 4D either!!
         *
         *
         * ⠀⠰⡿⠿⠛⠛⠻⠿⣷
         * ⠀⠀⠀⠀⠀⠀⣀⣄⡀⠀⠀⠀⠀⢀⣀⣀⣤⣄⣀⡀
         * ⠀⠀⠀⠀⠀⢸⣿⣿⣷⠀⠀⠀⠀⠛⠛⣿⣿⣿⡛⠿⠷
         * ⠀⠀⠀⠀⠀⠘⠿⠿⠋⠀⠀⠀⠀⠀⠀⣿⣿⣿⠇
         * ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠉⠁
         *
         *
         * ⠀⠀⠀⠀⣿⣷⣄⠀⢶⣶⣷⣶⣶⣤⣀
         * ⠀⠀⠀⠀⣿⣿⣿⠀⠀⠀⠀⠀⠈⠙⠻⠗
         * ⠀⠀⠀⣰⣿⣿⣿⠀⠀⠀⠀⢀⣀⣠⣤⣴⣶⡄
         * ⠀⣠⣾⣿⣿⣿⣥⣶⣶⣿⣿⣿⣿⣿⠿⠿⠛⠃
         * ⢰⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡄
         * ⢸⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡁
         * ⠈⢿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⠁
         * ⠀⠀⠛⢿⣿⣿⣿⣿⣿⣿⡿⠟
         * ⠀⠀⠀⠀⠀⠉⠉⠉
         *
         * @param x            X-coordinate for the location of this waypoint
         * @param y            Y-coordinate for the location of this waypoint
         * @param z            Z-coordinate for the location of this waypoint
         * @param theta        Heading at this waypoint
         * @param phi          Azimuth at this waypoint
         * @param speed        Approximately how fast the robot should be going by the time it reaches this waypoint
         * @param acceleration Maximum acceleration allowed to reach the target speed
         * @param deceleration Maximum deceleration allowed to reach the target speed
         * @return A waypoint with the specified properties
         * @see [Spherical Coordinates](http://mathworld.wolfram.com/SphericalCoordinates.html)
         */
        fun simple3D(x: Double, y: Double, z: Double, theta: Double, phi: Double, speed: Double, acceleration: Double, deceleration: Double): SplinePPWaypoint {
            val tanVec = ImmutableVector(
                    kTheta * Math.sin(phi) * Math.cos(theta),
                    kTheta * Math.sin(phi) * Math.sin(theta),
                    kTheta * Math.cos(phi)
            )
            return SplinePPWaypoint(ImmutableVector(x, y, z), tanVec, speed, acceleration, deceleration)
        }
    }
}
