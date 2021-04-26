package com.github.ezauton.core.pathplanning

//import com.github.ezauton.core.pathplanning.purepursuit.PPWaypoint
import java.io.Serializable

///**
// * Generates a path 🗺 for Pure Pursuit given [PPWaypoint]
// */
//class PP_PathGenerator(vararg ppWaypoints: PPWaypoint) : Serializable {
//
//  private val ppWaypoints: Array<PPWaypoint>
//
//  init {
//    this.ppWaypoints = ppWaypoints
//  }
//
//  fun generate(dt: Double): Path {
//    val pathSegments = ArrayList<PathSegment>()
//    var addedDistance = 0.0
//    for (i in 0 until ppWaypoints.size - 1) {
//      val from = ppWaypoints[i]
//      val to = ppWaypoints[i + 1]
//
//      // TODO: Update from RobotCode2018 style pathsegments
//      val pathSegment: LinearPathSegment
//      pathSegment = if (i == 0) {
//        val beginningSpeed = if (from.speed == 0.0) to.speed else from.speed
//
//        PathSegmentInterpolated(
//          from.location, to.location, i == ppWaypoints.size - 2, true, addedDistance,
//          beginningSpeed, to.speed, dt,
//          from.acceleration, from.deceleration
//        )
//      } else {
//        PathSegmentInterpolated(
//          from.location, to.location, i == ppWaypoints.size - 2, false, addedDistance,
//          from.speed, to.speed, dt,
//          from.acceleration, from.deceleration
//        )
//      }
//      addedDistance += pathSegment.length
//      pathSegments.add(pathSegment)
//    }
//    return Path.fromSegments(pathSegments)
//  }
//}
