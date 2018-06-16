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
}
