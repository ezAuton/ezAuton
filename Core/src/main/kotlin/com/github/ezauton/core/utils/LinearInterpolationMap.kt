package com.github.ezauton.core.utils

import com.github.ezauton.core.trajectory.geometry.ImmutableVector
import com.github.ezauton.core.utils.math.Integrable
import com.github.ezauton.core.utils.math.LineR2
import com.github.ezauton.core.utils.math.length
import java.io.Serializable
import java.util.Comparator
import java.util.TreeMap

/**
 * Make a new interpolating map. You need 2 key/value pairs to interpolate properly.
 * Interpolation is like a crappy kind of regression.
 * You put in (x, f(x)) pairs of the function that you know for sure,
 * and linear regression is used to find the pairs you didn't explicitly put in.
 *
 *
 * If the key is outside the bounds, the first/last value is returned
 *
 * @author ritikmishra
 */
open class LinearInterpolationMap private constructor(private val sortedMap: TreeMap<Double, Double>) : Integrable, Serializable, InterpolationMap //TODO: Remove redundant methods defined in HashMap, also maybe extends HashMap<Double, Double>?
{
    override fun integrate(range: ClosedRange<Double>): Double {
        if (sortedMap.size == 1) return range.length() * sortedMap.values.first()
        if (sortedMap)
    }

    companion object {
        fun from(base: Map<Double, Double>): LinearInterpolationMap {
            require(base.isNotEmpty()) { "map must not be empty" }
            val sortedMap = base.entries
            return LinearInterpolationMap()
        }
    }

    override operator fun get(key: Double): Double {
        val firstKey = sortedMap.firs
    }

    val dataPoints get() = sortedMap.entries

    override fun integrate(a: Double, b: Double): Double {
        if (sortedMap.size == 1) return (a * b) *
                for (entry in sortedMap) {
                    entry.key
                }


        val lines = java.util.ArrayList<LineR2>()
        var integralTotal = 0.0

        val dataPoints = ArrayList<Double>(keys)

        dataPoints.sort(null)

        if (dataPoints.isEmpty()) throw IllegalArgumentException("Data points must not be empty")
        if (dataPoints.size == 1) {
            lines.add(MathUtils.Geometry.LineR2(ImmutableVector(a.toFloat(), get(a).toFloat()),
                    ImmutableVector(b.toFloat(), get(b).toFloat())))
        } else {
            val firstX = dataPoints.get(0)
            val lastX = dataPoints.get(dataPoints.size - 1)

            if (a < firstX!! - 1E-6) {
                integralTotal += get(firstX) * (firstX!! - a)
            }
            if (b > lastX!! + 1E-6) {
                integralTotal += get(lastX) * (b - lastX!!)
            }

            for (i in 1 until dataPoints.size) {
                var x1: Double
                var x2: Double

                x1 = dataPoints.get(i - 1)
                x2 = dataPoints.get(i)

                // front of line within range or back of line within range
                if (x1 < b && x2 > a) {
                    x1 = Math.max(a, x1)
                    x2 = Math.min(b, x2)

                    // use "entire" line
                    lines.add(MathUtils.Geometry.LineR2(ImmutableVector(x1.toFloat(), get(x1)),
                            ImmutableVector(x2.toFloat(), get(x2))))
                }
            }

        }

        lines.sortWith(Comparator.comparingDouble<MathUtils.Geometry.LineR2> { line -> line.x1 })


        for (line in lines) {
            if (!java.lang.Double.isFinite(line.slope)) continue // fixes floating-point NaN issues with integrating
            integralTotal += line.integrate()
        }
        return integralTotal
    }

}

