package com.github.ezauton.core.pathplanning

import com.github.ezauton.conversion.*
import com.github.ezauton.core.record.Data
import kotlinx.serialization.Serializable



/**
 * A path is the conglomerate of several [PathSegment]s, which are in turn made from two [ScalarVector]s.
 * Thus, a Path is the overall Path that the robot will take formed by Waypoints.
 * This class is very helpful when it comes to tracking which segment is currently on and getting the distance
 * on the path at any point (taking arclength ... basically making path 1D).
 */
class Path<T : SIUnit<T>> constructor(val pathSegments: List<PathSegment<T>>) {


  val type get() = pathSegments[0].type

  val simpleRepr
    get(): Data.PathWrapper {
      val points = pathSegments.map { it.from } + pathSegments.last().to
      return Data.PathWrapper(points.map { it.scalarVector })
    }


  private val distances = run {
    val inner = DoubleArray(pathSegments.size + 1) { 0.0 }
    for (i in pathSegments.indices) {
      inner[i + 1] = inner[i] + pathSegments[i].length.value
    }

    inner
  }

  val start get() = pathSegments.first().from
  val end get() = pathSegments.last().to


  val distance = distances.last().withUnit(type)

  fun pointAtDist(dist: T, extrapolate: Boolean = false): ConcreteVector<T> {
    val value = dist.value

    if (value < 0) {
      require(extrapolate)
      return pathSegments.first().getAtDist(dist)
    }

    if (value > distance.value) {
      require(extrapolate)
      return pathSegments.last().getAtDist(dist - distance)
    }

    for (i in distances.indices) {
      val distanceBeforeI = distances[i]
      if (distanceBeforeI >= value) {
        val distanceBefore = distances[i - 1]
        val segment = pathSegments[i - 1]
        val relativeDistance = (value - distanceBefore).withUnit(type)
        return segment.getAtDist(relativeDistance)
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
