package com.github.ezauton.core.pathplanning.purepursuit

import com.github.ezauton.conversion.*
import com.github.ezauton.core.pathplanning.Trajectory
import com.github.ezauton.core.pathplanning.TrajectoryGenerator
import java.io.Serializable

interface WaypointAdder {
  fun point(x: Distance, y: Distance, speed: LinearVelocity, acceleration: LinearAcceleration, deceleration: LinearAcceleration)
}

fun trajectory(samplePeriod: Time, block: WaypointAdder.() -> Unit): Trajectory {
  val impl = PPWaypoint.Builder()
  impl.block()
  return impl.buildTrajectoryGenerator().generate(samplePeriod)
}

/**
 * Waypoint used in Pure Pursuit which includes translational location, speed, accel, decel...
 */
open class PPWaypoint
/**
 * Create a waypoint for Pure Pursuit to drive to
 *
 * @param location Where the waypoint is, given that the Y axis is the forward axis
 * @param speed Approximately how fast the robot should be going by the time it reaches this waypoint
 * @param acceleration Maximum acceleration allowed to reach the target speed
 * @param deceleration Maximum deceleration allowed to reach the target speed
 */
  (val location: ConcreteVector<Distance>, val speed: LinearVelocity, val acceleration: LinearAcceleration, val deceleration: LinearAcceleration) : Serializable {

  override fun toString(): String {
    return "PPWaypoint{" +
        "location=" + location +
        ", speed=" + speed +
        ", acceleration=" + acceleration +
        ", deceleration=" + deceleration +
        '}'.toString()
  }

  internal class Builder : WaypointAdder {
    private val waypointList = ArrayList<PPWaypoint>()

    override fun point(x: Distance, y: Distance, speed: LinearVelocity, acceleration: LinearAcceleration, deceleration: LinearAcceleration) {
      val decelerationCorrected = deceleration.abs() * (-1) // always be negative
      val waypoint = simple2D(x, y, speed, acceleration, decelerationCorrected)
      waypointList.add(waypoint)
    }

    fun buildArray(): Array<PPWaypoint> {
      return waypointList.toTypedArray()
    }

    fun buildTrajectoryGenerator(): TrajectoryGenerator {
      return TrajectoryGenerator(*buildArray())
    }

    fun flipY(): Builder {
      val ret = Builder()
      for (wp in waypointList) {
        ret.point(-wp.location.get(0), wp.location.get(1), wp.speed, wp.acceleration, wp.deceleration)
      }
      return ret
    }
  }

  companion object {

    /**
     * A shortcut to making a 2D waypoint
     *
     * @param x X-coordinate for the location of this waypoint
     * @param y Y-coordinate for the location of this waypoint
     * @param speed Approximately how fast the robot should be going by the time it reaches this waypoint
     * @param acceleration Maximum acceleration allowed to reach the target speed
     * @param deceleration Maximum deceleration allowed to reach the target speed
     * @return A waypoint with the specified properties
     */
    fun simple2D(x: Distance, y: Distance, speed: LinearVelocity, acceleration: LinearAcceleration, deceleration: LinearAcceleration): PPWaypoint {
      if (deceleration.isPositive) throw IllegalArgumentException("Deceleration cannot be positive!")
      return PPWaypoint(vec(x, y), speed, acceleration, deceleration)
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
     * @param x X-coordinate for the location of this waypoint
     * @param y Y-coordinate for the location of this waypoint
     * @param z Z-coordinate for the location of this waypoint
     * @param speed Approximately how fast the robot should be going by the time it reaches this waypoint
     * @param acceleration Maximum acceleration allowed to reach the target speed
     * @param deceleration Maximum deceleration allowed to reach the target speed
     * @return A waypoint with the specified properties
     */
    fun simple3D(x: Distance, y: Distance, z: Distance, speed: LinearVelocity, acceleration: LinearAcceleration, deceleration: LinearAcceleration): PPWaypoint {
      return PPWaypoint(vec(x, y, z), speed, acceleration, deceleration)
    }
  }
}
