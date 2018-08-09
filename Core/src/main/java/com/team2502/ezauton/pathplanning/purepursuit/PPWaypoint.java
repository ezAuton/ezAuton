package com.team2502.ezauton.pathplanning.purepursuit;

import com.team2502.ezauton.trajectory.geometry.ImmutableVector;

/**
 * Waypoint used in Pure Pursuit
 */
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

    /**
     * A shortcut to making a 2D waypoint
     *
     * @param x
     * @param y
     * @param speed
     * @param acceleration
     * @param deceleration
     * @return
     */
    public static PPWaypoint simple2D(double x, double y, double speed, double acceleration, double deceleration)
    {
        return new PPWaypoint(new ImmutableVector(x, y), speed, acceleration, deceleration);
    }

    /**
     * A shortcut to making a 3D waypoint (Deep Space will have drones so we need 3D PP) ... ofc you never
     * know when we will need 4D either!!
     * <p>
     * ⠀⠰⡿⠿⠛⠛⠻⠿⣷
     * ⠀⠀⠀⠀⠀⠀⣀⣄⡀⠀⠀⠀⠀⢀⣀⣀⣤⣄⣀⡀
     * ⠀⠀⠀⠀⠀⢸⣿⣿⣷⠀⠀⠀⠀⠛⠛⣿⣿⣿⡛⠿⠷
     * ⠀⠀⠀⠀⠀⠘⠿⠿⠋⠀⠀⠀⠀⠀⠀⣿⣿⣿⠇
     * ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠉⠁
     * <p>
     * ⠀⠀⠀⠀⣿⣷⣄⠀⢶⣶⣷⣶⣶⣤⣀
     * ⠀⠀⠀⠀⣿⣿⣿⠀⠀⠀⠀⠀⠈⠙⠻⠗
     * ⠀⠀⠀⣰⣿⣿⣿⠀⠀⠀⠀⢀⣀⣠⣤⣴⣶⡄
     * ⠀⣠⣾⣿⣿⣿⣥⣶⣶⣿⣿⣿⣿⣿⠿⠿⠛⠃
     * ⢰⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡄
     * ⢸⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡁
     * ⠈⢿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⠁
     * ⠀⠀⠛⢿⣿⣿⣿⣿⣿⣿⡿⠟
     * ⠀⠀⠀⠀⠀⠉⠉⠉
     *
     * @param x
     * @param y
     * @param z
     * @param speed
     * @param acceleration
     * @param deceleration
     * @return
     */
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
