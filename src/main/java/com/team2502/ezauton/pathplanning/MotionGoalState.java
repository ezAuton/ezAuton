package com.team2502.ezauton.pathplanning;

public class MotionGoalState
{

    private final double endPosition;
    private final double endVelocity;

    public MotionGoalState(double endPosition, double endVelocity)
    {
        this.endPosition = endPosition;
        this.endVelocity = endVelocity;
    }

    public double getEndPosition()
    {
        return endPosition;
    }

    public double getEndVelocity()
    {
        return endVelocity;
    }
}
