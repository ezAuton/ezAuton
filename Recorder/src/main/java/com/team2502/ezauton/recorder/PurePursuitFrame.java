package com.team2502.ezauton.recorder;

import org.github.ezauton.ezauton.trajectory.geometry.ImmutableVector;

public class PurePursuitFrame extends SequentialDataFrame
{
    private final double lookahead;
    private final ImmutableVector closestPoint;
    private final ImmutableVector goalPoint;
    private final double dCP;
    private final int currentSegmentIndex;

    public PurePursuitFrame(double time, double lookahead, ImmutableVector closestPoint, ImmutableVector goalPoint, double dCP, int currentSegmentIndex)
    {
        super(time);

        this.lookahead = lookahead;
        this.closestPoint = closestPoint;
        this.goalPoint = goalPoint;
        this.dCP = dCP;
        this.currentSegmentIndex = currentSegmentIndex;
    }

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
}
