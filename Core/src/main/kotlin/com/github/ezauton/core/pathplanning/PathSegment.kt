package com.github.ezauton.core.pathplanning

import com.github.ezauton.conversion.ConcreteVector
import com.github.ezauton.conversion.SIUnit

import java.io.Serializable
import kotlin.reflect.KClass


data class SegmentPoint<T : SIUnit<T>>(val value: ConcreteVector<T>, val tValue: Double)

/**
 * A section of a path (usually linear) which has similar laws (i.e. same transition between two speeds).
 */
interface PathSegment<T : SIUnit<T>> : Serializable {

  val from: ConcreteVector<T>
  val to: ConcreteVector<T>
  val type: KClass<out T>
  val length: T

  fun getPointAlong(proportion: Double): ConcreteVector<T>
  fun getClosestPoint(point: ConcreteVector<T>): SegmentPoint<T>
}


fun <T : SIUnit<T>> PathSegment<T>.getAtDist(dist: T): ConcreteVector<T> {
  val t = dist.value / length.value
  return getPointAlong(t)
}

fun <T : SIUnit<T>> PathSegment<T>.dist(point: ConcreteVector<T>): T {
  return getClosestPoint(point).value.dist(point)
}

fun <T : SIUnit<T>> PathSegment<T>.dist2(point: ConcreteVector<T>): T {
  return getClosestPoint(point).value.dist2(point)
}
