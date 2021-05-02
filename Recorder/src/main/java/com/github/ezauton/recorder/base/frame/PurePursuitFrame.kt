package com.github.ezauton.recorder.base.frame

import com.github.ezauton.recorder.SequentialDataFrame
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.ezauton.conversion.ScalarVector
import java.lang.StringBuilder

class PurePursuitFrame : SequentialDataFrame {
  @JsonProperty
  var lookahead = 0.0
    private set

  @JsonProperty
  var closestPoint: ScalarVector? = null
    private set

  @JsonProperty
  var goalPoint: ScalarVector? = null
    private set

  @JsonProperty
  private var dCP = 0.0

  @JsonProperty
  var currentSegmentIndex = 0
    private set

  constructor(time: Double, lookahead: Double, closestPoint: ScalarVector?, goalPoint: ScalarVector?, dCP: Double, currentSegmentIndex: Int) : super(time) {
    this.lookahead = lookahead
    this.closestPoint = closestPoint
    this.goalPoint = goalPoint
    this.dCP = dCP
    this.currentSegmentIndex = currentSegmentIndex
  }

  private constructor() {}

  fun getdCP(): Double {
    return dCP
  }

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
