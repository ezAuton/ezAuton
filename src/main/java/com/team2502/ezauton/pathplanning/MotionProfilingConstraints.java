package com.team2502.ezauton.pathplanning;

public class MotionProfilingConstraints
{

    private final double maxAcceleration;
    private final double maxVelocity;
    private final double minVelocity;
    private final double maxDeceleration;

    public MotionProfilingConstraints(double maxAcceleration, double maxVelocity, double minVelocity, double maxDeceleration)
    {
        this.maxAcceleration = maxAcceleration;
        this.maxVelocity = maxVelocity;
        this.minVelocity = minVelocity;
        this.maxDeceleration = maxDeceleration;
    }

    public double getMinVelocity()
    {
        return minVelocity;
    }

    public double getMaxDeceleration()
    {
        return maxDeceleration;
    }

    public double getMaxAcceleration()
    {
        return maxAcceleration;
    }

    public double getMaxVelocity()
    {
        return maxVelocity;
    }
}
