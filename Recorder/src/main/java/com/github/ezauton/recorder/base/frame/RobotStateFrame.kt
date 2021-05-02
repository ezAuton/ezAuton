package com.github.ezauton.recorder.base.frame

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.ezauton.conversion.ScalarVector
import com.github.ezauton.recorder.SequentialDataFrame
import java.io.Serializable

/**
 * An immutable object for storing the state of the robot at a particular point in time.
 *
 *
 * Generic drivetrain format
 */
open class RobotStateFrame(
  @JsonProperty val pos: ScalarVector,
  @JsonProperty val heading: Double = 0.0,
  @JsonProperty val robotWidth: Double = 0.0,
  @JsonProperty val robotLength: Double = 0.0,
  @JsonProperty val robotVelocity: ScalarVector,
  time: Double,
) : SequentialDataFrame(time), Serializable {


  override fun toString(): String {
    return "RobotState{" +
        "pos=" + pos +
        ", heading=" + heading +
        ", robotWidth=" + robotWidth +
        ", robotLength=" + robotLength +
        ", time=" + time +
        '}'
  }
}
