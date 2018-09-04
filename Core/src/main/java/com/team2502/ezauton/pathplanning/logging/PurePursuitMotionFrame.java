package com.team2502.ezauton.pathplanning.logging;

import com.team2502.ezauton.trajectory.geometry.ImmutableVector;

import java.io.Serializable;

/**
 * Data class that describes
 */
public class PurePursuitMotionFrame implements Serializable
{
    public final double usedLookahead;
    public final double goalPointX;
    public final double goalPointY;

    public final int currentSegmentIndex;
    public final double closestPointX;
    public final double closestPointY;
    public final double dCP;

    /**
     * Create a PurePursuitMotionFrame
     *
     * @param robotPos            Position of the robot
     * @param usedLookahead       The lookahead used for the calculations at this second
     * @param goalPoint           The point where the robot was trying to drive to
     * @param currentSegmentIndex The index of the current path segment that we are on
     * @param closestPoint        The closest point on our path to us
     */
    public PurePursuitMotionFrame(ImmutableVector robotPos, double usedLookahead, ImmutableVector goalPoint, int currentSegmentIndex, ImmutableVector closestPoint)
    {
        this.usedLookahead = usedLookahead;

        this.goalPointX = goalPoint.get(0);
        this.goalPointY = goalPoint.get(1);

        this.currentSegmentIndex = currentSegmentIndex;
        this.closestPointX = closestPoint.get(0);
        this.closestPointY = closestPoint.get(1);
        this.dCP = robotPos.dist(closestPoint);
    }

    public double getUsedLookahead()
    {
        return usedLookahead;
    }

    public double getGoalPointX()
    {
        return goalPointX;
    }

    public double getGoalPointY()
    {
        return goalPointY;
    }

    public int getCurrentSegmentIndex()
    {
        return currentSegmentIndex;
    }

    public double getClosestPointX()
    {
        return closestPointX;
    }

    public double getClosestPointY()
    {
        return closestPointY;
    }

    public double getdCP()
    {
        return dCP;
    }
}