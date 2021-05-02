package com.github.ezauton.recorder.base

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.ezauton.conversion.ScalarVector
import com.github.ezauton.recorder.base.frame.RobotStateFrame

class TankRobotState(
  @JsonProperty val leftVel: Double = 0.0, @JsonProperty val leftPos: Double = 0.0, @JsonProperty val rightVel: Double = 0.0, @JsonProperty val rightPos: Double = 0.0,
  time: Double,
  pos: ScalarVector,
  heading: Double,
  robotWidth: Double,
  robotLength: Double,
  robotVelocity: ScalarVector
) : RobotStateFrame(pos, heading, robotWidth, robotLength, robotVelocity, time) {

}
