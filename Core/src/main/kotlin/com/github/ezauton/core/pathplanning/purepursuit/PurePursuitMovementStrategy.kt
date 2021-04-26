package com.github.ezauton.core.pathplanning.purepursuit

import com.github.ezauton.conversion.*
import com.github.ezauton.core.pathplanning.PathProgressor
import com.github.ezauton.core.pathplanning.ProgressResult
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
  val pathProgressor: PathProgressor<Distance>,
  /**
   * How close we need to be to the final waypoint for us to decide that we are finished
   */
  private val stopTolerance: Distance,
  private val dataChannel: Channel<PurePursuitData>? = null
) {

  var isFinished = false
    private set

  init {
    require(stopTolerance <= 0) { "stopTolerance must be a positive number!" }
  }

  private val path get() = pathProgressor.path

  /**
   * @return The absolute location of the selected goal point.
   * The goal point is a point on the path 1 lookahead distance away from us.
   * We want to drive at it.
   * @see [Velocity and End Behavior
  ](https://www.chiefdelphi.com/forums/showthread.php?threadid=162713) */
  private fun calculateAbsoluteGoalPoint(currentDistance: Distance, lookAheadDistance: Distance): ConcreteVector<Distance> {
    require(currentDistance.isFinite) { "distanceCurrentSegmentLeft ($currentDistance) must be finite" }
    return path.pointAtDist(currentDistance + lookAheadDistance, extrapolate = true)
  }

  /**
   * @param loc Current position of the robot
   * @param lookahead Current lookahead as given by an Lookahead instance
   * @return The wanted pose of the robot at a certain location
   */
  fun update(loc: ConcreteVector<Distance>, lookahead: Distance): ConcreteVector<Distance>? {

    val currentDistance = when (val on = pathProgressor.progress(loc)) {
      is ProgressResult.End -> {
        isFinished = true
        return null
      };
      is ProgressResult.OnPath -> {
        val distanceLeft = path.distance - on.distance
        if (distanceLeft < stopTolerance) {
          isFinished = true
          return null
        }
        on.distance
      }
      is ProgressResult.Start -> zero()
    }

    val goalPoint = calculateAbsoluteGoalPoint(currentDistance, lookahead)

    if (dataChannel != null) {
      val data = PurePursuitData(goalPoint, isFinished, lookahead, closestPoint, closestPointDist, segmentOnI)
      val channel = Channel()
      dataChannel.send(data)
    }
    return goalPoint
  }
}

data class PurePursuitData(
  val goalPoint: ConcreteVector<Distance>,
  val finished: Boolean,
  val lookahead: Distance,
  val closestPoint: ScalarVector,
  val closestPointDist: Double,
  val currentSegmentIndex: Int
)
