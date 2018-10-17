package com.team2502.ezauton.recorder;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Describes a recording frame about time-based data
 * <br>
 * Essentially a recording for one instaneous second
 */
public abstract class SequentialDataFrame
{
    @JsonProperty
    protected double time;

    protected SequentialDataFrame(double time) {this.time = time;}

    protected SequentialDataFrame() {}
}
