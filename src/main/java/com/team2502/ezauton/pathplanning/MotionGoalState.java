package com.team2502.ezauton.pathplanning;

public class MotionGoalState
{

    private final double endPosition;
    private final double endSpeed;

    public MotionGoalState(double endPosition, double endSpeed)
    {
        this.endPosition = endPosition;
        this.endSpeed = endSpeed;
    }

    public double getEndPosition()
    {
        return endPosition;
    }

    public double getEndSpeed()
    {
        return endSpeed;
    }
}
