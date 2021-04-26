package com.github.ezauton.core.pathplanning

import com.github.ezauton.conversion.ConcreteVector
import com.github.ezauton.conversion.SIUnit

private const val UNINITIALIZED = -1

sealed class ProgressResult<T : SIUnit<T>> {
  class Start<T : SIUnit<T>> : ProgressResult<T>()
  class End<T : SIUnit<T>> : ProgressResult<T>()
  class OnPath<T : SIUnit<T>>(val point: SegmentPoint<T>, val segment: PathSegment<T>) : ProgressResult<T>()
}

class PathProgressor<T : SIUnit<T>>(val path: Path<T>) {


  var segmentOnIdx = -1
  val segmentOn get() = path[segmentOnIdx]


  fun progress(point: ConcreteVector<T>): ProgressResult<T> {
    if (segmentOnIdx == UNINITIALIZED) segmentOnIdx = closestIdx(point)
    return findClosest(point)
  }

  private fun findClosest(point: ConcreteVector<T>): ProgressResult<T> {

    while (true) {
      val segmentPoint = segmentOn.getClosestPointTo(point)
      val t = segmentPoint.tValue


      when {
        t > 1 -> {
          segmentOnIdx += 1
        }
        t < 0 -> {
          segmentOnIdx -= 0;
        }
        else -> {
          return ProgressResult.OnPath(segmentPoint, segmentOn)
        }
      }

      when {
        segmentOnIdx < 0 -> return ProgressResult.Start()
        segmentOnIdx >= path.pathSegments.size -> return ProgressResult.End()
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
