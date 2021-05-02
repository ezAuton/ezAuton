package com.github.ezauton.recorder.base.frame

import com.github.ezauton.recorder.SequentialDataFrame
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.ezauton.conversion.ScalarVector
import java.lang.StringBuilder

class PurePursuitFrame(
  time: Double, @JsonProperty val lookahead: Double, @JsonProperty val closestPoint: ScalarVector, @field:JsonProperty val goalPoint: ScalarVector, @JsonProperty
  private val dCP: Double, @JsonProperty val currentSegmentIndex: Int
) : SequentialDataFrame(time) {

  override fun toString(): String {
    val sb = StringBuilder("PurePursuitFrame{")
    sb.append("lookahead=").append(lookahead)
    sb.append(", closestPoint=").append(closestPoint)
    sb.append(", goalPoint=").append(goalPoint)
    sb.append(", dCP=").append(dCP)
    sb.append(", currentSegmentIndex=").append(currentSegmentIndex)
    sb.append('}')
    return sb.toString()
  }
}
