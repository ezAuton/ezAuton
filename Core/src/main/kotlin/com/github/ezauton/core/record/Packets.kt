package com.github.ezauton.core.record

import com.github.ezauton.conversion.*
import kotlinx.serialization.Serializable


@OptIn(ExperimentalUnsignedTypes::class)
interface ForRobot {
  val robotId: Int
}

interface AtTime {
  val time: Time
}

data class SimpleSegment(val from: ScalarVector, val to: ScalarVector)

@OptIn(ExperimentalUnsignedTypes::class)
@Serializable
sealed class Data {

  @Serializable
  data class PositionInit constructor(val basePosition: ScalarVector, override val robotId: Int) : Data(), ForRobot

  @Serializable
  data class TankInit constructor(val width: Double, val height: Double, override val robotId: Int) : Data(), ForRobot

  @Serializable
  data class StateChange(val pos: ScalarVector, val robotLength: Distance = zero(), val robotVelocity: ScalarVector, override val time: Time, override val robotId: Int) : Data(), ForRobot, AtTime

  @Serializable
  data class DriveInput(val attemptLeftVal: Double, val attemptRightVel: Double, override val robotId: Int, override val time: Time) : Data(), ForRobot, AtTime

  @Serializable
  data class PurePursuit(
    val goalPoint: ScalarVector,
    val finished: Boolean,
    val lookahead: Distance,
    val closestPoint: ScalarVector,
    val closestPointDist: Double,
    val currentSegmentIndex: Int
  ) : Data()


  @Serializable
  data class TREE(
    val leftWheelVelocity: LinearVelocity,
    val rightWheelVelocity: LinearVelocity,
    val heading: Angle,
    val location: ScalarVector
  ): Data()


  @Serializable
  data class PathWrapper(val points: List<ScalarVector>): Data() {
    val segments get() = points.windowed(2).map { (a,b) -> SimpleSegment(a,b) }
  }
}
