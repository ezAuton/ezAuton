package org.github.ezauton.ezauton.pathplanning.purepursuit;

import org.github.ezauton.ezauton.pathplanning.PP_PathGenerator;
import org.github.ezauton.ezauton.pathplanning.QuinticSpline;
import org.github.ezauton.ezauton.trajectory.geometry.ImmutableVector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Waypoint used in Pure Pursuit
 */
public class SplinePPWaypoint extends PPWaypoint implements Serializable
{
    public static class Builder
    {
        private List<SplinePPWaypoint> waypointList = new ArrayList<>();

        public Builder add(double x, double y, double xPrime, double yPrime, double speed, double acceleration, double deceleration)
        {
            SplinePPWaypoint waypoint = SplinePPWaypoint.simple2D(x, y, xPrime, yPrime, speed, acceleration, deceleration);
            waypointList.add(waypoint);
            return this;
        }

        public PP_PathGenerator buildPathGenerator()
        {
            if(waypointList.size() > 1)
            {
                List<QuinticSpline> splines = new ArrayList<>();
                for(int i = 1; i < waypointList.size(); i++)
                {
                    SplinePPWaypoint prevWP = waypointList.get(i - 1);
                    SplinePPWaypoint thisWP = waypointList.get(i);
                    splines.add(new QuinticSpline(prevWP.getLocation(), thisWP.getLocation(), prevWP.tanVec, thisWP.tanVec));
                }
                return new PP_PathGenerator(QuinticSpline.toPathSegments(splines, waypointList));
            }
            else
            {
                throw new IllegalStateException("Cannot create spline with less than 2 waypoints");
            }
        }
    }

    private final ImmutableVector tanVec;

    /**
     * Create a waypoint for Pure Pursuit to drive to
     *
     * @param location     Where the waypoint is, given that the Y axis is the forward axis
     * @param tanVec       The direction the robot should be pointing in at this waypoint. A larger magnitude means the spline is straighter.
     * @param speed        Approximately how fast the robot should be going by the time it reaches this waypoint
     * @param acceleration Maximum acceleration allowed to reach the target speed
     * @param deceleration Maximum deceleration allowed to reach the target speed
     */
    //TODO: Confirm documentation is accurate
    public SplinePPWaypoint(ImmutableVector location, ImmutableVector tanVec, double speed, double acceleration, double deceleration)
    {
        super(location, speed, acceleration, deceleration);
        this.tanVec = tanVec;
    }

    /**
     * A shortcut to making a 2D waypoint
     *
     * @param x            X-coordinate for the location of this waypoint
     * @param y            Y-coordinate for the location of this waypoint
     * @param xPrime       X-component for vector tangent to the spline at this waypoint
     * @param yPrime       Y-component for vector tangent to the spline at this waypoint
     * @param speed        Approximately how fast the robot should be going by the time it reaches this waypoint
     * @param acceleration Maximum acceleration allowed to reach the target speed
     * @param deceleration Maximum deceleration allowed to reach the target speed
     * @return A waypoint with the specified properties
     */
    public static SplinePPWaypoint simple2D(double x, double y, double xPrime, double yPrime, double speed, double acceleration, double deceleration)
    {
        if(deceleration > 0) { throw new IllegalArgumentException("Deceleration cannot be positive!"); }
        return new SplinePPWaypoint(new ImmutableVector(x, y), new ImmutableVector(xPrime, yPrime), speed, acceleration, deceleration);
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
    public static SplinePPWaypoint simple3D(double x, double y, double z, double xPrime, double yPrime, double zPrime, double speed, double acceleration, double deceleration)
    {
        return new SplinePPWaypoint(new ImmutableVector(x, y, z), new ImmutableVector(xPrime, yPrime, zPrime), speed, acceleration, deceleration);
    }


    public ImmutableVector getTanVec()
    {
        return tanVec;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder("SplinePPWaypoint{");
        sb.append("location=").append(getLocation());
        sb.append(", speed=").append(getSpeed());
        sb.append(", acceleration=").append(getAcceleration());
        sb.append(", deceleration=").append(getDeceleration());
        sb.append(", tanVec=").append(tanVec);
        sb.append('}');
        return sb.toString();
    }
}
