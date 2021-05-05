package com.github.ezauton.core.record

import com.github.ezauton.conversion.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString


@OptIn(ExperimentalUnsignedTypes::class)
interface ForRobot {
  val robotId: Int
}

@Serializable
data class Packet(val data: Data, val sentTime: Time) {
  fun toJson() = format.encodeToString(this)
}

data class SimpleSegment(val from: ScalarVector, val to: ScalarVector)

@OptIn(ExperimentalUnsignedTypes::class)
@Serializable
sealed class Data {

  @Serializable
  class PositionInit constructor(val basePosition: ScalarVector, override val robotId: Int) : Data(), ForRobot

  @Serializable
  data class TankInit constructor(val width: Double, val height: Double, override val robotId: Int) : Data(), ForRobot

  @Serializable
  data class StateChange(val pos: ScalarVector, val robotLength: Distance = zero(), val robotVelocity: ScalarVector, override val robotId: Int) : Data(), ForRobot

  @Serializable
  data class DriveInput(val attemptLeftVal: Double, val attemptRightVel: Double, override val robotId: Int) : Data(), ForRobot

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
  data class TankRobotState(
    val leftWheelVelocity: LinearVelocity,
    val rightWheelVelocity: LinearVelocity,
    val heading: Angle,
    val location: ScalarVector
  ) : Data()


  @Serializable
  data class PathWrapper(val points: List<ScalarVector>) : Data() {
    val segments get() = points.windowed(2).map { (a, b) -> SimpleSegment(a, b) }
  }
}
