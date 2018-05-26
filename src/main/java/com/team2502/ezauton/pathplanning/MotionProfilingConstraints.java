package com.team2502.ezauton.pathplanning;

public class MotionProfilingConstraints
{

    private final double maxAcceleration;
    private final double maxSpeed;

    public MotionProfilingConstraints(double maxSpeed, double maxAcceleration)
    {
        this.maxAcceleration = maxAcceleration;
        this.maxSpeed = maxSpeed;
    }

    public double getMaxAcceleration()
    {
        return maxAcceleration;
    }

    public double getMaxSpeed()
    {
        return maxSpeed;
    }
}
