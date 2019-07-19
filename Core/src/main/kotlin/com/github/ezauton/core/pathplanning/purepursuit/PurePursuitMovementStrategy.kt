package com.github.ezauton.core.pathplanning.purepursuit

import com.github.ezauton.conversion.ConcreteVector
import com.github.ezauton.conversion.Distance
import com.github.ezauton.core.pathplanning.Path
import com.github.ezauton.conversion.ScalarVector
import kotlinx.coroutines.channels.Channel

/**
 * The main logic behind Pure Pursuit ... returns the subsequent location the robot should try to
 * go towards.
 */
class PurePursuitMovementStrategy
/**
 * Strategize your movement!
 *
 * @param path The path to drive along
 * @param stopTolerance How close we need to be to the final waypoint for us to decide that we are finished
 */(
        /**
         * The path that we're driving on
         */
        val path: Path<Distance>,
        /**
         * How close we need to be to the final waypoint for us to decide that we are finished
         */
        private val stopTolerance: Double,
        private val dataChannel: Channel<PurePursuitData>? = null
) {

    var isFinished = false
        private set

    init {
        require(stopTolerance <= 0) { "stopTolerance must be a positive number!" }
    }

    /**
     * @return The absolute location of the selected goal point.
     * The goal point is a point on the path 1 lookahead distance away from us.
     * We want to drive at it.
     * @see [Velocity and End Behavior
    ](https://www.chiefdelphi.com/forums/showthread.php?threadid=162713) */
    private fun calculateAbsoluteGoalPoint(distanceCurrentSegmentLeft: Double, lookAheadDistance: Distance): ScalarVector {
        require(distanceCurrentSegmentLeft.isFinite()) { "distanceCurrentSegmentLeft ($distanceCurrentSegmentLeft) must be finite" }

        // The intersections with the path we are following and the circle around the robot of
        // radius lookAheadDistance. These intersections will determine the "goal point" we
        // will generate an arc to go to.

        val goalPoint = path.getGoalPoint(distanceCurrentSegmentLeft, lookAheadDistance)
        if (!goalPoint.isFinite) throw IllegalStateException("Logic error. goal point $goalPoint should be finite.")
        return goalPoint
    }

    /**
     * @param loc Current position of the robot
     * @param lookahead Current lookahead as given by an Lookahead instance
     * @return The wanted pose of the robot at a certain location
     */
    fun update(loc: ConcreteVector<Distance>, lookahead: Distance): ConcreteVector<Distance>? {
        val current = path
        val segmentOnI = path.segmentOnI
        val currentClosestPoint = current.getClosestPoint(loc)
        val closestPoint = path.getClosestPoint(loc) // why do we not get closest point on current line segment???

        if (closestPoint != currentClosestPoint) {
            val locAgain = path.getClosestPoint(loc)
            throw IllegalStateException("not equal closest points")
        }
        val currentDistance = current.getAbsoluteDistance(closestPoint)
        val distanceLeftSegment = current.absoluteDistanceEnd - currentDistance
        val closestPointDist = closestPoint.dist(loc)

        if (distanceLeftSegment < 0) {
            if (path.progressIfNeeded(distanceLeftSegment, closestPointDist, loc).size != 0) {
                return update(loc, lookahead) // progresses recursively until at right point
            }
        }

        val finalDistance = path.length

        val distanceLeftTotal = finalDistance - currentDistance

        if (distanceLeftTotal < stopTolerance) {
            isFinished = true
            //            return null;
        }

        path.progressIfNeeded(distanceLeftSegment, closestPointDist, loc)
        val goalPoint = calculateAbsoluteGoalPoint(distanceLeftSegment, lookahead)

        if (dataChannel != null) {
            val data = PurePursuitData(goalPoint, isFinished, lookahead, closestPoint, closestPointDist, segmentOnI)
            val channel = Channel()
            dataChannel.send(data)
        }
        return goalPoint
    }
}

data class PurePursuitData(val goalPoint: ConcreteVector<Distance>, val finished: Boolean, val lookahead: Distance, val closestPoint: ScalarVector, val closestPointDist: Double, val currentSegmentIndex: Int)
