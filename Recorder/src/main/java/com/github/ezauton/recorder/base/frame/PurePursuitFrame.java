package com.github.ezauton.recorder.base.frame;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.ezauton.recorder.SequentialDataFrame;
import com.github.ezauton.core.trajectory.geometry.ImmutableVector;

public class PurePursuitFrame extends SequentialDataFrame
{
    @JsonProperty
    private double lookahead;

    @JsonProperty
    private ImmutableVector closestPoint;

    @JsonProperty
    private ImmutableVector goalPoint;

    @JsonProperty
    private double dCP;

    @JsonProperty
    private int currentSegmentIndex;

    public PurePursuitFrame(double time, double lookahead, ImmutableVector closestPoint, ImmutableVector goalPoint, double dCP, int currentSegmentIndex)
    {
        super(time);

        this.lookahead = lookahead;
        this.closestPoint = closestPoint;
        this.goalPoint = goalPoint;
        this.dCP = dCP;
        this.currentSegmentIndex = currentSegmentIndex;
    }

    private PurePursuitFrame() {}

    public double getLookahead()
    {
        return lookahead;
    }

    public ImmutableVector getClosestPoint()
    {
        return closestPoint;
    }

    public ImmutableVector getGoalPoint()
    {
        return goalPoint;
    }

    public double getdCP()
    {
        return dCP;
    }

    public int getCurrentSegmentIndex()
    {
        return currentSegmentIndex;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder("PurePursuitFrame{");
        sb.append("lookahead=").append(lookahead);
        sb.append(", closestPoint=").append(closestPoint);
        sb.append(", goalPoint=").append(goalPoint);
        sb.append(", dCP=").append(dCP);
        sb.append(", currentSegmentIndex=").append(currentSegmentIndex);
        sb.append('}');
        return sb.toString();
    }
}
