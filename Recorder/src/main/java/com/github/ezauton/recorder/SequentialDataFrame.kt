package com.github.ezauton.recorder

import com.fasterxml.jackson.annotation.JsonProperty


/**
 * Describes a recording frame about time-based data
 * <br></br>
 * Essentially a recording for one instant
 */
abstract class SequentialDataFrame {
  @JsonProperty
  var time = 0.0
    protected set

  protected constructor(time: Double) {
    this.time = time
  }

  protected constructor() {}
}
