package com.github.ezauton.recorder.base.frame

import com.github.ezauton.recorder.SequentialDataFrame
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

class TankDriveableFrame(time: Double, @JsonProperty val attemptLeftVel: Double, @JsonProperty val attemptRightVel: Double) : SequentialDataFrame(time), Serializable {

  override fun toString(): String {
    return "TankDriveableFrame{" +
        "attemptLeftVel=" + attemptLeftVel +
        ", attemptRightVel=" + attemptRightVel +
        '}'
  }
}
