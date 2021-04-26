package com.github.ezauton.core.pathplanning

import com.github.ezauton.conversion.ConcreteVector
import com.github.ezauton.conversion.SIUnit
import com.github.ezauton.conversion.ScalarVector
import com.github.ezauton.conversion.withUnit
import java.lang.IllegalStateException

/**
 * A path is the conglomerate of several [PathSegment]s, which are in turn made from two [ScalarVector]s.
 * Thus, a Path is the overall Path that the robot will take formed by Waypoints.
 * This class is very helpful when it comes to tracking which segment is currently on and getting the distance
 * on the path at any point (taking arclength ... basically making path 1D).
 */
class Path<T : SIUnit<T>> constructor(val pathSegments: List<PathSegment<T>>) {


  val type get() = pathSegments[0].type

  private val distances = DoubleArray(pathSegments.size + 1) { 0.0 }



  init {
    for (i in pathSegments.indices) {
      distances[i + 1] = distances[i] + pathSegments[i].length.value
    }
  }

  val distance = distances.last().withUnit(type)

  fun pointAtDist(dist: T, extrapolate: Boolean = false): ConcreteVector<T> {
    val value = dist.value

    if(value < 0){
      require(extrapolate)
      return pathSegments.first().getAtDist(dist)
    }

    if(value > distance.value){
      require(extrapolate)
      return pathSegments.last().getAtDist(dist - distance)
    }

    for (i in distances.indices) {
      val distanceBeforeI = distances[i]
      if (distanceBeforeI >= value) {
        val distanceBefore = distances[i - 1]
        val segment = pathSegments[i - 1]
        val relativeDistance = value - distanceBefore
        return segment.getAtDist(relativeDistance.withUnit(type))
      }
    }

    throw IllegalStateException()
  }

  fun distanceBeforeIdx(idx: Int): Double {
    return distances[idx]
  }

  operator fun get(segmentOnIdx: Int): PathSegment<T> {
    return pathSegments[segmentOnIdx]
  }

  init {
    require(pathSegments.isNotEmpty())
  }
}
