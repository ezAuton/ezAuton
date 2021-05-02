package com.github.ezauton.core.pathplanning

import com.github.ezauton.conversion.*

/**
 * A mostly-implemented linear PathSegment which contains all methods save getSpeed(...).
 *
 *
 * Create a LinearPathSegment
 *
 * @param from Starting location
 * @param to Ending location
 * @param finish If this is the last path segment in the path
 * @param beginning If this is the first path segment in the path
 * @param distanceStart How far along the path the starting location is (arclength)
 */
class LineSegment<T : SIUnit<T>>(override val from: ConcreteVector<T>, override val to: ConcreteVector<T>) : PathSegment<T> {

  override val length = from.dist(to)
  override val type get() = from.type

  val n = (from - to).scalarVector / length.value

  init {
    if (length.isApproxZero) {
      throw IllegalArgumentException("PathSegment length must be non-zero.")
    }
  }

  val slope
    get(): Double {
      if ((to.x - from.x).isApproxZero) {
        return Double.POSITIVE_INFINITY
      }
      return (to.y - from.y) / (to.x - from.x)
    }

  fun integrate(): Double {
    // TODO: hard to implement with unit
    val dx = to.x - from.x
    val averageY = (from.y + to.y) / 2

    return (dx.value * averageY.value)
  }

  override fun getClosestPoint(point: ConcreteVector<T>): SegmentPoint<T> {
    val (a, _, p) = scalar(from, to, point)
    val ap = p - a
    val t = ap.dot(n)
    val x = a + t * n //  x is a point on line
    return SegmentPoint(x.withUnit(type), t)
  }

  override fun getPointAlong(proportion: Double): ConcreteVector<T> {
    return (from.scalarVector + n * proportion).withUnit(type)
  }

}
