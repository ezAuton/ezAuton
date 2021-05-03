package com.github.ezauton.core.pathplanning

import com.github.ezauton.conversion.Distance
import com.github.ezauton.conversion.LinearVelocity
import com.github.ezauton.conversion.Time
import com.github.ezauton.conversion.withUnit
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

    var lengthOn = 0.0

    for (i in 0 until ppWaypoints.size - 1) {
      val from = ppWaypoints[i]
      val to = ppWaypoints[i + 1]

      val line = LineSegment(from.location, to.location)
      segments.add(line)

      val length = line.length


      val beginningSpeed = if (i == 0 && from.speed.isApproxZero) to.speed else from.speed
      val interpolator = SpeedInterpolator(length, beginningSpeed, to.speed, dt, from.acceleration, from.deceleration)

      lengthOn += length.value

      interpolatorMap[lengthOn] = interpolator

    }

    val sortedMap = interpolatorMap.toSortedMap()

    val speed = TimeGetter(sortedMap)

    val path = Path(segments)

    return Trajectory(path, speed)
  }
}

private class TimeGetter(val map: SortedMap<Double, SpeedInterpolator>) : Speed {

  override fun invoke(distance: Distance): LinearVelocity {
    val tailMap = map.tailMap(distance.value)

    val to = if (tailMap.isEmpty()) map.lastKey() else tailMap.firstKey()

    val headMap = map.headMap(distance.value)
    val from = if(headMap.isEmpty()) 0.0 else headMap.lastKey()

    println("interpolator $from -> $to")
    val interpolator =  map[to]!!
    val relativeDist = distance.value - from
    println("dist ${distance.value}")
    println("relative $relativeDist")
    return interpolator[relativeDist.withUnit()]
  }

}
