package com.github.ezauton.core.pathplanning

import com.github.ezauton.core.trajectory.geometry.ImmutableVector
import com.github.ezauton.core.utils.math.epsilonEquals
import com.github.ezauton.core.utils.math.getClosestPointLineSegments

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
abstract class LinearPathSegment(
    final override val from: ImmutableVector,
    final override val to: ImmutableVector,
    override val isFinish: Boolean,
    override val isBeginning: Boolean,
    distanceStart: Double
) : PathSegment {

    /**
     * @return How far along the entire path that the from point is
     */
    final override val absoluteDistanceStart: Double
    /**
     * @return How far along the entire path that the end point is
     */
    final override val absoluteDistanceEnd: Double
    private val dPos: ImmutableVector
    final override val length: Double = this.from.dist(this.to)

    init {
        if (0.0 epsilonEquals length) {
            throw IllegalArgumentException("PathSegment length must be non-zero.")
        }
        this.absoluteDistanceStart = distanceStart
        this.absoluteDistanceEnd = distanceStart + length
        dPos = to.sub(from)
    }

    /**
     * Get the point on the line segment that is the closest to the robot
     *
     * @param robotPos The position of the robot
     * @return The point on the line segment that is the closest to the robot
     */
    override fun getClosestPoint(robotPos: ImmutableVector): ImmutableVector {
        return getClosestPointLineSegments(from, to, robotPos)
    }

    /**
     * Calculate how far along the path a point on the linesegment is
     *
     * @param linePos The point on the line
     * @return How far it is along the line segment
     */
    override fun getAbsoluteDistance(linePos: ImmutableVector): Double {
        if (to == linePos) {
            return absoluteDistanceEnd
        }

        if (from == linePos) {
            return absoluteDistanceStart
        }

        // The difference between from, truncating 0
        val dif = linePos.sub(from)
        for (i in 0 until dif.elements.size) {
            val difElement = dif.get(i)
            if (difElement != 0.0) {
                val dPosElement = dPos.get(i)
                //                assert dPosElement != 0;
                //                if(dPos.get(i) == 0) throw new IllegalArgumentException("Point must be on the line!");
                val proportion = difElement / dPosElement
                return absoluteDistanceStart + proportion * length
            }
        }
        throw ArithmeticException("Somehow dif has a dimension of 0.")
    }

    /**
     * Convert absolute distance (dist. along the path) to relative distance (dist. along this segment)
     *
     * @param absoluteDistance The absolute distance along the path
     * @return The relative distance along this path segment
     */
    fun getRelativeDistance(absoluteDistance: Double): Double {
        return absoluteDistance - absoluteDistanceStart
    }

    abstract override fun getSpeed(absoluteDistance: Double): Double

    /**
     * Assert a point that is x distance along the whole path is in this path segment
     *
     * @param absoluteDistance How far the point is along the path
     * @throws IllegalArgumentException
     */
    private fun checkDistance(absoluteDistance: Double) {
        if (absoluteDistance in absoluteDistanceStart..absoluteDistanceEnd) throw IllegalArgumentException("Must be within bounds")
    }

    /**
     * Based on the relative distance of a point along this path segment, get the location of the point
     *
     * @param relativeDistance How far the point is along this path segment
     * @return The aboslute location of the point
     */
    override fun getPoint(relativeDistance: Double): ImmutableVector {
        return dPos.mul(relativeDistance / length).plus(from)
    }

    /**
     * Get the distance left squared
     *
     * @param point A point on this path segment
     * @return The distance left on the path segment, squared
     */
    @Deprecated("")
    fun getDistanceLeft2(point: ImmutableVector): Double {
        return to.sub(point).mag2()
    }

    override fun toString(): String {
        return "PathSegment{" +
            "from=" + from +
            ", to=" + to +
            '}'.toString()
    }
}
