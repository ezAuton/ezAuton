package com.github.ezauton.recorder;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Describes a recording frame about time-based data
 * <br>
 * Essentially a recording for one instant
 */
public abstract class SequentialDataFrame
{
    @JsonProperty
    protected double time;

    protected SequentialDataFrame(double time) {this.time = time;}

    protected SequentialDataFrame() {}

    public double getTime()
    {
        return time;
    }
}
