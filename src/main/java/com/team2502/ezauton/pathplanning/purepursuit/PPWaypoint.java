package com.team2502.ezauton.pathplanning.purepursuit;

import com.team2502.ezauton.trajectory.geometry.ImmutableVector;

public class PPWaypoint
{

    private final ImmutableVector location;
    private final double speed;
    private final double acceleration;
    private final double deceleration;

    public PPWaypoint(ImmutableVector location, double speed, double acceleration, double deceleration)
    {
        this.location = location;
        this.speed = speed;
        this.acceleration = acceleration;
        this.deceleration = deceleration;
    }

    public static PPWaypoint simple2D(double x, double y, double speed, double acceleration, double deceleration)
    {
        return new PPWaypoint(new ImmutableVector(x, y), speed, acceleration, deceleration);
    }

    public static PPWaypoint simple3D(double x, double y, double z, double speed, double acceleration, double deceleration)
    {
        return new PPWaypoint(new ImmutableVector(x, y, z), speed, acceleration, deceleration);
    }

    public ImmutableVector getLocation()
    {
        return location;
    }

    public double getSpeed()
    {
        return speed;
    }

    public double getAcceleration()
    {
        return acceleration;
    }

    public double getDeceleration()
    {
        return deceleration;
    }
}
