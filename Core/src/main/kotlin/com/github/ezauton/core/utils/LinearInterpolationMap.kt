package com.github.ezauton.core.utils

import com.github.ezauton.conversion.Scalar
import com.github.ezauton.conversion.vec
import com.github.ezauton.core.pathplanning.LineSegment
import com.github.ezauton.core.utils.math.Integrable
import java.io.Serializable
import java.util.*


/**
 * Make a new interpolating map. You need 2 key/value pairs to interpolate properly.
 * Interpolation is like a crappy kind of regression.
 * You put in (x, f(x)) pairs of the function that you know for sure,
 * and linear regression is used to find the pairs you didn't explicitly put in.
 *
 *
 * If the key is outside the bounds, the first/last value is returned
 *
 * @author ritikmishra, andrewgazelka
 */
class LinearInterpolationMap private constructor(private var sortedMap: SortedMap<Double, Double>) : Integrable, Serializable, InterpolationMap {

  companion object {
    fun from(base: Map<Double, Double>): LinearInterpolationMap {
      require(base.isNotEmpty()) { "map must not be empty" }
      val sortedMap = base.toSortedMap()
      return LinearInterpolationMap(sortedMap)
    }
  }

  operator fun set(key: Double, value: Double){
    sortedMap[key] = value
  }

//  override operator fun get(key: Double): Double {
//    val firstKey = sortedMap.firs
//  }


  override fun integrate(range: ClosedRange<Double>): Double {
    val rangeStart = range.start
    val rangeEnd = range.endInclusive

    val lines = ArrayList<LineSegment<Scalar>>()
    var integralTotal = 0.0

    require(sortedMap.isNotEmpty()) { "The interpolation map must have data" }

    if (sortedMap.size == 1) {
      lines.add(LineSegment(vec(rangeStart, get(rangeStart)), vec(rangeEnd, get(rangeEnd))))
    } else {

      val firstX = sortedMap.firstKey()
      val lastX = sortedMap.lastKey()

      if (rangeStart < firstX!! - 1E-6) {
        integralTotal += get(firstX) * (firstX - rangeStart)
      }
      if (rangeEnd > lastX!! + 1E-6) {
        integralTotal += get(lastX) * (rangeEnd - lastX)
      }

      val allEntries = sortedMap.entries.toList()
      for (i in 1 until allEntries.size) {
        var x1 = allEntries[i - 1].key
        var x2 = allEntries[i].key

        // front of line within range or back of line within range
        if (x1 < rangeEnd && x2 > rangeStart) {
          x1 = rangeStart.coerceAtLeast(x1)
          x2 = rangeEnd.coerceAtMost(x2)

          // use "entire" line
          lines.add(
            LineSegment(
              vec(x1, get(x1)),
              vec(x2, get(x2))
            )
          )
        }
      }
    }

    lines.sortBy { line -> line.from.x }

    for (line in lines) {
      if (line.slope.isFinite()) integralTotal += line.integrate()
    }

    return integralTotal
  }

  /**
   * Use linear regression to estimate what your "f(x)" will give when evaluated at the Double `key`
   *
   * @param key The Double to evaluate "f(x)" at
   * @return The estimated value of "f(key)"
   */
  override operator fun get(key: Double): Double {

    if (sortedMap.size == 1) {
      return sortedMap[sortedMap.firstKey()]!!
    }

    val exactValue = sortedMap[key]

    if (exactValue != null) {
      return exactValue
    }

    val firstX = sortedMap.firstKey()

    if (key <= firstX) return sortedMap[firstX]!!

    val lastX = sortedMap.lastKey()

    if (key >= lastX) return sortedMap[lastX]!!


    sortedMap.entries.asSequence().windowed(2, step = 1).forEach { (from, to) ->
      val f = from.key
      val t = to.key
      if (f < key && key < t) {
        val distFrom = key - f
        val totalDist = t - f
        val proportionTo = distFrom / totalDist

        return proportionTo * to.value + (1 - proportionTo) * from.value
      }
    }

    throw IllegalStateException("somehow didn't find a result")

  }
}
