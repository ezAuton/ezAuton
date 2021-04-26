package com.github.ezauton.core.pathplanning

import com.github.ezauton.conversion.ConcreteVector
import com.github.ezauton.conversion.SIUnit
import com.github.ezauton.conversion.withUnit

private const val UNINITIALIZED = -1

enum class Position {
  START,
  MIDDLE,
  END
}

sealed class ProgressResult<T : SIUnit<T>>(val segment: PathSegment<T>, val closestPoint: SegmentPoint<T>, val distance: T, val segmentIdx: Int) {
  class Start<T : SIUnit<T>>(segment: PathSegment<T>, point: SegmentPoint<T>, distance: T, segmentIdx: Int) : ProgressResult<T>(segment, point, distance, segmentIdx)
  class End<T : SIUnit<T>>(segment: PathSegment<T>, point: SegmentPoint<T>, distance: T, segmentIdx: Int) : ProgressResult<T>(segment, point, distance, segmentIdx)
  class OnPath<T : SIUnit<T>>(segment: PathSegment<T>, point: SegmentPoint<T>, distance: T, val position: Position, segmentIdx: Int) : ProgressResult<T>(segment, point, distance, segmentIdx)
}

class PathProgressor<T : SIUnit<T>>(val path: Path<T>) {

  var segmentOnIdx = -1
  val type get() = path[0].type
  val segmentOn get() = path[segmentOnIdx]


  fun progress(point: ConcreteVector<T>): ProgressResult<T> {
    if (segmentOnIdx == UNINITIALIZED) segmentOnIdx = closestIdx(point)
    return findClosest(point)
  }

  private fun findClosest(point: ConcreteVector<T>): ProgressResult<T> {

    while (true) {
      val segmentPoint = segmentOn.getClosestPoint(point)
      val t = segmentPoint.tValue


      when {
        t > 1 -> {
          segmentOnIdx += 1
        }
        t < 0 -> {
          segmentOnIdx -= 0;
        }
        else -> {
          val distanceBefore = path.distanceBeforeIdx(segmentOnIdx)
          val distanceAt = distanceBefore + t * segmentOn.length.value

          val position = when (segmentOnIdx) {
            path.pathSegments.lastIndex -> Position.END
            0 -> Position.START
            else -> Position.MIDDLE
          }

          return ProgressResult.OnPath(segmentOn, segmentPoint, distanceAt.withUnit(type), position, segmentOnIdx)
        }
      }

      when {
        segmentOnIdx < 0 -> {
          segmentOnIdx = 0
          return ProgressResult.Start(segmentOn, SegmentPoint(segmentOn.from, 0.0), 0.0.withUnit(type), segmentOnIdx)
        }
        segmentOnIdx >= path.pathSegments.size -> {
          segmentOnIdx = path.pathSegments.lastIndex
          return ProgressResult.End(segmentOn, SegmentPoint(segmentOn.to, 1.0), path.distance, segmentOnIdx)
        }
      }

    }
  }

  private fun closestIdx(point: ConcreteVector<T>): Int {
    val (minIdx, _) = path.pathSegments.mapIndexed { index, pathSegment ->
      index to pathSegment
    }.minByOrNull { (_, pathSegment) -> pathSegment.dist2(point) }!!

    return minIdx;
  }

}

private fun onEdge(t: Double, epsilon: Double = 0.001): Boolean {
  return t < epsilon || t > (1 - epsilon)
}
