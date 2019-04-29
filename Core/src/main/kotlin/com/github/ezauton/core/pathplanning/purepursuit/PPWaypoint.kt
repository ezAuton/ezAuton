package com.github.ezauton.core.pathplanning.purepursuit

import com.github.ezauton.core.pathplanning.PP_PathGenerator
import com.github.ezauton.core.trajectory.geometry.ImmutableVector

import java.io.Serializable
import java.util.ArrayList

/**
 * Waypoint used in Pure Pursuit which includes translational location, speed, accel, decel...
 */
open class PPWaypoint
/**
 * Create a waypoint for Pure Pursuit to drive to
 *
 * @param location     Where the waypoint is, given that the Y axis is the forward axis
 * @param speed        Approximately how fast the robot should be going by the time it reaches this waypoint
 * @param acceleration Maximum acceleration allowed to reach the target speed
 * @param deceleration Maximum deceleration allowed to reach the target speed
 */
//TODO: Confirm documentation is accurate
(val location: ImmutableVector, val speed: Double, val acceleration: Double, val deceleration: Double) : Serializable {

    override fun toString(): String {
        return "PPWaypoint{" +
                "location=" + location +
                ", speed=" + speed +
                ", acceleration=" + acceleration +
                ", deceleration=" + deceleration +
                '}'.toString()
    }

    class Builder {
        private val waypointList = ArrayList<PPWaypoint>()

        fun add(x: Double, y: Double, speed: Double, acceleration: Double, deceleration: Double): Builder {
            val waypoint = PPWaypoint.simple2D(x, y, speed, acceleration, deceleration)
            waypointList.add(waypoint)
            return this
        }

        fun buildArray(): Array<PPWaypoint> {
            return waypointList.toTypedArray()
        }

        fun buildPathGenerator(): PP_PathGenerator {
            return PP_PathGenerator(*buildArray())
        }

        fun flipY(): Builder {
            val ret = Builder()
            for (wp in waypointList) {
                ret.add(-wp.location.get(0), wp.location.get(1), wp.speed, wp.acceleration, wp.deceleration)
            }
            return ret
        }
    }

    companion object {

        /**
         * A shortcut to making a 2D waypoint
         *
         * @param x            X-coordinate for the location of this waypoint
         * @param y            Y-coordinate for the location of this waypoint
         * @param speed        Approximately how fast the robot should be going by the time it reaches this waypoint
         * @param acceleration Maximum acceleration allowed to reach the target speed
         * @param deceleration Maximum deceleration allowed to reach the target speed
         * @return A waypoint with the specified properties
         */
        fun simple2D(x: Double, y: Double, speed: Double, acceleration: Double, deceleration: Double): PPWaypoint {
            if (deceleration > 0) throw IllegalArgumentException("Deceleration cannot be positive!")
            return PPWaypoint(ImmutableVector(x, y), speed, acceleration, deceleration)
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
         * @param speed        Approximately how fast the robot should be going by the time it reaches this waypoint
         * @param acceleration Maximum acceleration allowed to reach the target speed
         * @param deceleration Maximum deceleration allowed to reach the target speed
         * @return A waypoint with the specified properties
         */
        fun simple3D(x: Double, y: Double, z: Double, speed: Double, acceleration: Double, deceleration: Double): PPWaypoint {
            return PPWaypoint(ImmutableVector(x, y, z), speed, acceleration, deceleration)
        }
    }
}
