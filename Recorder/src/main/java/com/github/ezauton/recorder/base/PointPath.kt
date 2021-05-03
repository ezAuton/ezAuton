package com.github.ezauton.recorder.base

import com.github.ezauton.conversion.Distance
import com.github.ezauton.conversion.ScalarVector
import com.github.ezauton.core.pathplanning.Path
import kotlinx.serialization.Serializable


class Segment(val from: ScalarVector, val to: ScalarVector)

@Serializable
class PointPath(val points: List<ScalarVector>) {
  companion object {
    fun from(path: Path<Distance>): PointPath {
      val points = path.pathSegments.map { it.from.scalarVector } + path.end.scalarVector
      return PointPath(points)
    }
  }

  val segments get() = points.windowed(2).map { (from, to) -> Segment(from, to)}


}
