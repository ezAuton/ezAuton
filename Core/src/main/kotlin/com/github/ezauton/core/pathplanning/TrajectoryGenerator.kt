package com.github.ezauton.core.pathplanning

import com.github.ezauton.conversion.Distance
import com.github.ezauton.conversion.LinearVelocity
import com.github.ezauton.conversion.Time
import com.github.ezauton.core.action.Speed
import com.github.ezauton.core.pathplanning.purepursuit.PPWaypoint
import java.io.Serializable
import java.util.*

class Trajectory(val path: Path<Distance>, val speed: (Distance) -> LinearVelocity)

private typealias Segment = PathSegment<Distance>

/**
 * Generates a path 🗺 for Pure Pursuit given [PPWaypoint]
 */
class TrajectoryGenerator(private vararg val ppWaypoints: PPWaypoint) : Serializable {

  fun generate(dt: Time): Trajectory {

    val interpolatorMap = HashMap<Double, SpeedInterpolator>()

    val segments = ArrayList<Segment>()
    for (i in 0 until ppWaypoints.size - 1) {
      val from = ppWaypoints[i]
      val to = ppWaypoints[i + 1]

      val line = LineSegment(from.location, to.location)
      segments.add(line)

      val length = line.length


      val beginningSpeed = if (i == 0 && from.speed.isApproxZero) to.speed else from.speed
      val interpolator = SpeedInterpolator(length, beginningSpeed, to.speed, dt, from.acceleration, from.deceleration)

      interpolatorMap[length.value] = interpolator

    }

    val sortedMap = interpolatorMap.toSortedMap()

    val speed = TimeGetter(sortedMap)

    val path = Path(segments)

    return Trajectory(path, speed)
  }
}

private class TimeGetter(val map: SortedMap<Double, SpeedInterpolator>) : Speed {

  private fun interpolator(distance: Distance): SpeedInterpolator {
    val tailMap = map.tailMap(distance.value)
    println("passed in dist ${distance.value}")
    val key = if (tailMap.isEmpty()) map.lastKey() else tailMap.firstKey()
    return map[key]!!
  }

  override fun invoke(distance: Distance): LinearVelocity {
    val interp = interpolator(distance)
    return interp[distance]
  }

}
