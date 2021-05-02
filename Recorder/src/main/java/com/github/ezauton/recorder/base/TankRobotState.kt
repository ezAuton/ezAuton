package com.github.ezauton.recorder.base

import com.github.ezauton.conversion.ScalarVector
import kotlinx.serialization.Serializable

@Serializable
class TankRobotState(
  val leftVel: Double = 0.0, val leftPos: Double = 0.0, val rightVel: Double = 0.0, val rightPos: Double = 0.0,
  val time: Double,
  val pos: ScalarVector,
  val heading: Double,
  val robotWidth: Double,
  val robotLength: Double,
  val robotVelocity: ScalarVector
){ // : TODO RobotStateFrame(pos, heading, robotWidth, robotLength, robotVelocity, time) {

}
