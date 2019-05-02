package com.github.ezauton.core.pathplanning

import com.github.ezauton.core.pathplanning.purepursuit.PPWaypoint
import com.github.ezauton.core.trajectory.geometry.ImmutableVector
import com.github.ezauton.core.utils.LinearInterpolationMap
import java.util.ArrayList

class QuinticSpline(val first: ImmutableVector, val last: ImmutableVector, val firstSlope: ImmutableVector, val lastSlope: ImmutableVector) : MathUtils.Geometry.ParametricFunction {

    /**
     * t^5 coefficient for the quintic spline equation
     */
    private val a: ImmutableVector

    /**
     * t^4 coefficient for the quintic spline equation
     */
    private val b: ImmutableVector

    /**
     * t^3 coefficient for the quintic spline equation
     */
    private val c: ImmutableVector

    /**
     * t^2 coefficient for the quintic spline equation
     */
    private val d: ImmutableVector

    /**
     * t^1 coefficient for the quintic spline equation
     */
    private val e: ImmutableVector

    /**
     * t^0 coefficient for the quintic spline equation
     */
    private val f: ImmutableVector

    val length: Double
        get() = getArcLength(0.0, 1.0)

    val equation: String
        get() {
            val sb = StringBuilder()
            sb.append("(")
            sb.append(String.format("%f t^5 + %f t^4 + %f t^3 + %f t^2 + %f t + %f", a.get(0), b.get(0), c.get(0), d.get(0), e.get(0), f.get(0)))
            sb.append(",")
            sb.append(String.format("%f t^5 + %f t^4 + %f t^3 + %f t^2 + %f t + %f", a.get(1), b.get(1), c.get(1), d.get(1), e.get(1), f.get(0)))
            sb.append(")")
            return sb.toString()
        }

    init {

        // -3 (2p_0 - 2p_1 + p'_0 + p'_1)
        a = first.mul(2.0)
                .sub(last.mul(2.0))
                .add(firstSlope)
                .add(lastSlope).mul(-3.0)

        //  15 p_0 - 15 p_1 + 8p'_0 + 7p'_1
        b = first.mul(15.0)
                .sub(last.mul(15.0))
                .add(firstSlope.mul(8.0))
                .add(lastSlope.mul(7.0))

        // -2 ( 5p_0 - 5p_1 + 3p'_0 + 2p'_1 )
        c = first.mul(5.0)
                .sub(last.mul(5.0))
                .add(firstSlope.mul(3.0))
                .add(lastSlope.mul(2.0)).mul(-2.0)

        // 0
        d = ImmutableVector(0, 0)

        // p'_0
        e = firstSlope

        // p_0
        f = first
    }

    constructor(first: ImmutableVector, last: ImmutableVector, firstTheta: Double, lastTheta: Double) : this(first,
            last,
            ImmutableVector(Math.cos(firstTheta), Math.sin(firstTheta)).mul(1.2 * first.dist(last)),
            ImmutableVector(Math.cos(lastTheta), Math.sin(lastTheta)).mul(1.2 * first.dist(last))
    )

    fun getPoint(relativeDistance: Double): ImmutableVector {
        return fromArcLength(relativeDistance)
    }

    fun getDistanceLeft2(point: ImmutableVector): Double {
        return MathUtils.pow2(getDistanceLeft(point))
    }

    fun getDistanceLeft(point: ImmutableVector): Double {
        return getArcLength(0.0, 1.0) - getArcLength(0.0, getT(point, 0.0, 1.0))
    }

    override fun toString(): String {
        return "QuinticSpline{" +
                "first=" + first +
                ", last=" + last +
                ", firstSlope=" + firstSlope +
                ", lastSlope=" + lastSlope +
                '}'.toString()
    }

    /**
     * @param t:[0,1]
     * @return
     */
    override fun getX(t: Double): Double {
        return MathUtils.pow5(t) * a.get(0) + MathUtils.pow4(t) * b.get(0) + MathUtils.pow3(t) * c.get(0) + MathUtils.pow2(t) * d.get(0) + t * e.get(0) + f.get(0)
    }

    /**
     * @param t:[0,1]
     * @return
     */
    override fun getY(t: Double): Double {
        return MathUtils.pow5(t) * a.get(1) + MathUtils.pow4(t) * b.get(1) + MathUtils.pow3(t) * c.get(1) + MathUtils.pow2(t) * d.get(1) + t * e.get(1) + f.get(1)
    }

    companion object {
        private val FEET_PER_SUBDIVISION = 1 / 3.0

        /**
         * Using a few linear path segments (for motion information) and a few splines (for location information), create lots of small linear path segments
         * in the shape of the splines.
         *
         * @param splines A list of splines
         * @param segments A list of path segments
         * @return An array of spline-shaped path segments
         */
        fun toPathSegments(splines: List<QuinticSpline>, segments: List<PPWaypoint>): Array<PPWaypoint> {
            var isError = false

            // check if the spline points don't match up with the path waypoints

            try {
                for (i in splines.indices) {
                    if (i < segments.size) {
                        val hasSameStartingPoint = splines[i].first == segments[i].location
                        val hasSameEndingPoint = splines[i].last == segments[i + 1].location
                        if (!hasSameStartingPoint || !hasSameEndingPoint) {
                            isError = true
                            break
                        }
                    } else {
                        isError = true
                        break
                    }
                }

                if (isError) {
                    // TODO: More descriptive error message
                    throw RuntimeException("your splines don't intersect your path at the right spots (the waypoints")
                }
            } catch (e: IndexOutOfBoundsException) {
                throw RuntimeException("your splines don't intersect your path at the right spots (the waypoints", e)
            }

            val retList = ArrayList<PPWaypoint>()

            for (s in splines.indices) {
                val fromWaypoint = segments[s]
                val toWaypoint = segments[s + 1]
                val currentSpline = splines[s]

                // Maps t to speed
                val speedMap = LinearInterpolationMap(0.0, fromWaypoint.speed)
                speedMap[1.0] = toWaypoint.speed

                val accelMap = LinearInterpolationMap(0.0, fromWaypoint.acceleration)
                accelMap[1.0] = toWaypoint.acceleration

                val decelMap = LinearInterpolationMap(0.0, fromWaypoint.deceleration)
                decelMap[1.0] = toWaypoint.deceleration

                val numSubdivisions = (splines[s].length / FEET_PER_SUBDIVISION).toInt() // subdivision approximately every 4 inches
                val tInterval = 1.0 / numSubdivisions
                val end = if (s == splines.size - 1) 1 else 1 - tInterval
                var t = 0.0
                while (t < end) {
                    val loc = currentSpline.get(t)
                    val newWaypoint = PPWaypoint(loc, speedMap.get(t), fromWaypoint.acceleration, fromWaypoint.deceleration)
                    retList.add(newWaypoint)
                    t += tInterval
                }
            }
            val retArray = arrayOfNulls<PPWaypoint>(retList.size)
            retList.toTypedArray()
            return retArray
        }
    }
}
