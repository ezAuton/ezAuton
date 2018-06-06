package com.team2502.ezauton.pathplanning;

public class MotionProfilingConstraints
{

    private final double maxAcceleration;
    private final double maxSpeed;
    private final double maxDeceleration;

    public MotionProfilingConstraints(double maxAcceleration, double maxSpeed, double minSpeed, double maxDeceleration)
    {
        this.maxAcceleration = maxAcceleration;
        this.maxSpeed = maxSpeed;
        this.maxDeceleration = maxDeceleration;
    }

    public double getMaxDeceleration()
    {
        return maxDeceleration;
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
