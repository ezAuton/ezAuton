package com.github.ezauton.core.pathplanning.purepursuit;

import com.github.ezauton.core.pathplanning.PP_PathGenerator;
import com.github.ezauton.core.trajectory.geometry.ImmutableVector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Waypoint used in Pure Pursuit which includes translational location, speed, accel, decel...
 */
public class PPWaypoint implements Serializable
{

    public static class Builder {
        private List<PPWaypoint> waypointList = new ArrayList<>();

        public Builder add(double x, double y, double speed, double acceleration, double deceleration)
        {
            PPWaypoint waypoint = PPWaypoint.simple2D(x, y, speed, acceleration, deceleration);
            waypointList.add(waypoint);
            return this;
        }

        public PPWaypoint[] buildArray(){
            return waypointList.toArray(new PPWaypoint[0]);
        }
        public PP_PathGenerator buildPathGenerator(){
            return new PP_PathGenerator(buildArray());
        }
    }

    private final ImmutableVector location;
    private final double speed;
    private final double acceleration;
    private final double deceleration;

    /**
     * Create a waypoint for Pure Pursuit to drive to
     *
     * @param location     Where the waypoint is, given that the Y axis is the forward axis
     * @param speed        Approximately how fast the robot should be going by the time it reaches this waypoint
     * @param acceleration Maximum acceleration allowed to reach the target speed
     * @param deceleration Maximum deceleration allowed to reach the target speed
     */
    //TODO: Confirm documentation is accurate
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
     * @param x            X-coordinate for the location of this waypoint
     * @param y            Y-coordinate for the location of this waypoint
     * @param speed        Approximately how fast the robot should be going by the time it reaches this waypoint
     * @param acceleration Maximum acceleration allowed to reach the target speed
     * @param deceleration Maximum deceleration allowed to reach the target speed
     * @return A waypoint with the specified properties
     */
    public static PPWaypoint simple2D(double x, double y, double speed, double acceleration, double deceleration)
    {
        if(deceleration > 0) throw new IllegalArgumentException("Deceleration cannot be positive!");
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
     * @param x            X-coordinate for the location of this waypoint
     * @param y            Y-coordinate for the location of this waypoint
     * @param z            Z-coordinate for the location of this waypoint
     * @param speed        Approximately how fast the robot should be going by the time it reaches this waypoint
     * @param acceleration Maximum acceleration allowed to reach the target speed
     * @param deceleration Maximum deceleration allowed to reach the target speed
     * @return A waypoint with the specified properties
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

    @Override
    public String toString()
    {
        return "PPWaypoint{" +
               "location=" + location +
               ", speed=" + speed +
               ", acceleration=" + acceleration +
               ", deceleration=" + deceleration +
               '}';
    }
}
