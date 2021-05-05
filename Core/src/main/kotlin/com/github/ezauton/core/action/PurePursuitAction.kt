package com.github.ezauton.core.action

import com.github.ezauton.conversion.*
import com.github.ezauton.core.localization.TransLocEst
import com.github.ezauton.core.pathplanning.PathProgressor
import com.github.ezauton.core.pathplanning.Trajectory
import com.github.ezauton.core.pathplanning.purepursuit.Lookahead
import com.github.ezauton.core.pathplanning.purepursuit.PurePursuitMovementStrategy
import com.github.ezauton.core.pathplanning.purepursuit.Update
import com.github.ezauton.core.record.Data
import com.github.ezauton.core.record.RecordingContext
import com.github.ezauton.core.robot.subsystems.TransLocDrivable
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.consumeAsFlow


typealias Speed = (distance: Distance) -> LinearVelocity

/**
 * A Pure Pursuit action which can be used in simulation or as a WPILib Command
 *
 * @param period How often to update estimated position, robot control, etc
 * @param translationalLocationEstimator An object that knows where we are on a 2D plane
 * @param lookahead An instance of [Lookahead] that can tell us how far along the path to look ahead
 * @param translationalLocationDrivable The drivetrain of the robot
 */
fun purePursuit(
  period: Period,
  trajectory: Trajectory,
  translationalLocationEstimator: TransLocEst,
  translationalLocationDrivable: TransLocDrivable,
  lookahead: Lookahead,
  stopDistance: Distance = 0.01.m,
) = action {

  val recordingContext = coroutineContext[RecordingContext]
  val dataChannel = if (recordingContext == null) null else Channel<Data.PurePursuit>()

  recordingContext?.recording?.receiveFlow(dataChannel!!.consumeAsFlow())

  val progressor = PathProgressor(trajectory.path)
  val speedFunction = trajectory.speed
  val ppMoveStrat = PurePursuitMovementStrategy(progressor, stopDistance, dataChannel)

  coroutineScope {

    periodic(period) { loop ->

      val currentLocation = translationalLocationEstimator.estimateLocation()

      when (val update = ppMoveStrat.update(currentLocation, lookahead.lookahead)) {
        is Update.Finished -> {
          translationalLocationDrivable.driveSpeed(0.0.mps)
          loop.stop()
        }
        is Update.Result -> {
          val speedUsed = speedFunction(update.on.distance)
          translationalLocationDrivable.driveTowardTransLoc(speedUsed, update.goal)
        }
      }
    }

  }
  dataChannel?.close()
  return@action
}

fun <T> T.purePursuit(period: Period, trajectory: Trajectory, lookahead: Lookahead, stopDistance: Distance = 0.01.m) where T : TransLocEst, T : TransLocDrivable =
  purePursuit(period, trajectory, this, this, lookahead, stopDistance)

fun <T> T.purePursuit(period: Time, trajectory: Trajectory, lookahead: Lookahead, stopDistance: Distance = 0.01.m) where T : TransLocEst, T : TransLocDrivable =
  purePursuit(Period(period), trajectory, this, this, lookahead, stopDistance)
