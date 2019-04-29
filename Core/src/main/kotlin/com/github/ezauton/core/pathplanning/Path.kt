package com.github.ezauton.core.pathplanning

import com.github.ezauton.core.trajectory.geometry.ImmutableVector
import java.util.*

/**
 * A path is the conglomerate of several [PathSegment]s, which are in turn made from two [ImmutableVector]s.
 * Thus, a Path is the overall Path that the robot will take formed by Waypoints.
 * This class is very helpful when it comes to tracking which segment is currently on and getting the distance
 * on the path at any point (taking arclength ... basically making path 1D).
 */
class Path private constructor(private val pathSegments: List<PathSegment>): Iterable<PathSegment> {

    override fun iterator(): Iterator<PathSegment> {
        var segmentOnI = -1
        lateinit var current: PathSegment
        private var closestPoint: ImmutableVector? = null
        private var robotLocationClosestPoint: ImmutableVector? = null
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

        val next: PathSegment
        get() {
            val nextSegmentI = segmentOnI + 1
            return if (nextSegmentI >= pathSegments.size) {
                null
            } else pathSegments[nextSegmentI]
        }
    }

    /**
     * arclength of path
     */
    val length: Double

    init {
        val last = pathSegments[pathSegments.size - 1]
        length = last.absoluteDistanceEnd
        moveNextSegment()
    }

    val start: ImmutableVector
        get() = pathSegments[0].from

    val end: ImmutableVector
        get() = pathSegments[pathSegments.size - 1].to

    /**
     * Moves to the next path segment
     *
     * @return If there was a next path segment to progress to
     */
    fun moveNextSegment(): Boolean {
        if (segmentOnI < pathSegments.size - 1) {
            segmentOnI++
            current = pathSegments[segmentOnI]
            return true
        }
        return false
    }

    fun exists(): Boolean {
        return pathSegments.isNotEmpty()
    }

    fun getClosestPoint(origin: ImmutableVector) // TODO: it might be better to not look purely at the current pathsegment and instead previous path segments
            : ImmutableVector? {

        // Commented out because if the PATH changes, this will not give the right result, even if the LOCATION is the same.
        // TODO: figure out a way to put some type of cache back in
        //        if(this.robotLocationClosestPoint != null && MathUtils.epsilonEquals(this.robotLocationClosestPoint, origin))
        //        {
        // ISSUE what if the path changed during this time!!!!!!!!! :o
        //            return closestPoint;
        //        }

        this.robotLocationClosestPoint = origin
        val current = current
        closestPoint = current.getClosestPoint(origin)
        return closestPoint
    }

    /**
     * Calculate the goal point that we should be driving at
     *
     * @param distanceLeftCurrentSegment The distance left before we complete our segment
     * @param lookahead                  Our current lookahead distance
     * @return Where we should drive at
     */
    fun getGoalPoint(distanceLeftCurrentSegment: Double, lookahead: Double): ImmutableVector? {
        var lookaheadCounter = lookahead
        val current = current
        // If our circle intersects on the assertSameDim path
        if (lookaheadCounter < distanceLeftCurrentSegment || current.isFinish) {
            val relativeDistance = current.length - distanceLeftCurrentSegment + lookaheadCounter
            return current.getPoint(relativeDistance)
        } else {
            lookaheadCounter -= distanceLeftCurrentSegment

            for (pathSegment in pathSegments) {
                val length = pathSegment.length
                if (lookaheadCounter > length && !pathSegment.isFinish) {
                    lookaheadCounter -= length
                } else {
                    return pathSegment.getPoint(lookaheadCounter)
                }
            }
        } // If our circle intersects other segments
        return null
    }


    //TODO: make this better

    /**
     * @param distanceLeftSegment The distance left before we are on the next path segment
     * @param closestPointDist    The distance to the closest point on the current path segment
     * @param robotPos            The location of the robot
     * @return The PathSegments that have been progressed
     */
    fun progressIfNeeded(distanceLeftSegment: Double, closestPointDist: Double, robotPos: ImmutableVector): List<PathSegment> {
        //TODO: Move magic number
        // Move to the next segment if we are near the end of the current segment
        if (distanceLeftSegment < .16f) {
            if (moveNextSegment()) {
                return listOf(pathSegments[segmentOnI - 1])
            }
        }

        // For all paths 2 feet ahead of us, progress on the path if we can
        //TODO: Move magic number
        val pathSegments = nextSegmentsInclusive(2.0)
        var i = segmentOnI
        for ((j, pathSegment) in pathSegments.withIndex()) {
            if (shouldProgress(pathSegment, robotPos, closestPointDist)) {
                moveSegment(i, pathSegment)
                return pathSegments.subList(0, j + 1)
            }
            i++
        }
        return emptyList()
    }

    /**
     * Move to another path segment
     *
     * @param segmentOnI The index of the path segment
     * @param segmentOn  The instance of the path segment
     */
    fun moveSegment(segmentOnI: Int, segmentOn: PathSegment) {
        this.segmentOnI = segmentOnI
        this.current = segmentOn
    }

    /**
     * Check if we should progress to another segment
     *
     * @param segment              The instance of this "other" segment
     * @param robotPos             The position of the robot
     * @param currentSegmentCPDist The distance to the closest point on the current segment
     * @return If we should progress to this "other" path segment
     */
    fun shouldProgress(segment: PathSegment?, robotPos: ImmutableVector, currentSegmentCPDist: Double): Boolean {
        if (segment == null)
        // we are on the last segment... we cannot progress
        {
            return false
        }

        val closestPoint = segment.getClosestPoint(robotPos)
        val nextClosestPointDistance = closestPoint.dist(robotPos)
        // TODO: Move magic number
        return currentSegmentCPDist > nextClosestPointDistance + 0.5f
    }


    fun getAbsDistanceOfClosestPoint(closestPoint: ImmutableVector): Double {
        val current = current
        val firstLocation = current.from
        return current.absoluteDistanceStart + firstLocation.dist(closestPoint)
    }

    /**
     * @param maxAheadDistance The distance to look ahead from the last segment
     * @return The segments that lay on the path between our current position and maxAheadDistance from our current position. This result includes the current path segment.
     */
    fun nextSegmentsInclusive(maxAheadDistance: Double): List<PathSegment> {
        val segments = ArrayList<PathSegment>()
        val startSegment = current
        segments.add(startSegment)
        val distanceStart = startSegment.absoluteDistanceEnd
        for (i in segmentOnI + 1 until pathSegments.size) {
            val pathSegment = pathSegments[i]
            if (pathSegment.absoluteDistanceStart - distanceStart < maxAheadDistance) {
                segments.add(pathSegment)
            } else {
                break
            }
        }
        return segments
    }

    companion object {

        /**
         * Create a path from multiple path segments
         *
         * @param pathSegments A List of IPathSegments
         * @return A path consisting of these segments
         */
        fun fromSegments(pathSegments: List<PathSegment>): Path {
            if (pathSegments.isEmpty()) throw IllegalArgumentException("Path must have at least one segment")

            val path = Path()
            path.pathSegments = ArrayList(pathSegments)
            val last = pathSegments[pathSegments.size - 1]
            path.length = last.absoluteDistanceEnd
            path.moveNextSegment()
            return path
        }

        fun fromSegments(vararg pathSegments: PathSegment): Path {
            return fromSegments(Arrays.asList(*pathSegments))
        }
    }
}
