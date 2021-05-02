package com.github.ezauton.recorder.base.frame

import com.github.ezauton.recorder.SequentialDataFrame
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

class TankDriveableFrame : SequentialDataFrame, Serializable {
  @JsonProperty
  var attemptLeftVel = 0.0
    private set

  @JsonProperty
  var attemptRightVel = 0.0
    private set

  constructor(time: Double, attemptLeftVel: Double, attemptRightVel: Double) : super(time) {
    this.attemptLeftVel = attemptLeftVel
    this.attemptRightVel = attemptRightVel
  }

  override fun toString(): String {
    return "TankDriveableFrame{" +
        "attemptLeftVel=" + attemptLeftVel +
        ", attemptRightVel=" + attemptRightVel +
        '}'
  }
}
