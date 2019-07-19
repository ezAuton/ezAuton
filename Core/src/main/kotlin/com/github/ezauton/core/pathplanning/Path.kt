package com.github.ezauton.core.pathplanning

import com.github.ezauton.conversion.SIUnit
import com.github.ezauton.conversion.ScalarVector

/**
 * A path is the conglomerate of several [PathSegment]s, which are in turn made from two [ScalarVector]s.
 * Thus, a Path is the overall Path that the robot will take formed by Waypoints.
 * This class is very helpful when it comes to tracking which segment is currently on and getting the distance
 * on the path at any point (taking arclength ... basically making path 1D).
 */
class Path<T: SIUnit<T>>  private constructor(private val pathSegments: List<PathSegment>)  {

}
