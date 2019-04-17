package com.github.ezauton.core.pathplanning.purepursuit;


import com.github.ezauton.core.pathplanning.PP_PathGenerator;
import com.github.ezauton.core.pathplanning.QuinticSpline;
import com.github.ezauton.core.trajectory.geometry.ImmutableVector;
import com.github.ezauton.core.utils.MathUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Waypoint used in Pure Pursuit
 */
public class SplinePPWaypoint extends PPWaypoint implements Serializable {

    private static final double kTheta = 1.2;
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
    public SplinePPWaypoint(ImmutableVector location, ImmutableVector tanVec, double speed, double acceleration, double deceleration) {
        super(location, speed, acceleration, deceleration);
        this.tanVec = tanVec;
    }

    public SplinePPWaypoint(ImmutableVector location, double theta, double speed, double acceleration, double deceleration) {
        super(location, speed, acceleration, deceleration);
        this.tanVec = new ImmutableVector(Math.cos(theta) * kTheta, Math.sin(theta) * kTheta);
    }

    /**
     * A shortcut to making a 2D waypoint
     *
     * @param x            X-coordinate for the location of this waypoint
     * @param y            Y-coordinate for the location of this waypoint
     * @param theta        Angle that the robot should be at when it reaches this waypoint
     * @param speed        Approximately how fast the robot should be going by the time it reaches this waypoint
     * @param acceleration Maximum acceleration allowed to reach the target speed
     * @param deceleration Maximum deceleration allowed to reach the target speed
     * @return A waypoint with the specified properties
     */
    public static SplinePPWaypoint simple2D(double x, double y, double theta, double speed, double acceleration, double deceleration) {
        if (deceleration > 0) {
            throw new IllegalArgumentException("Deceleration cannot be positive!");
        }
        return new SplinePPWaypoint(new ImmutableVector(x, y), theta, speed, acceleration, deceleration);
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
     * @param theta        Heading at this waypoint
     * @param phi          Azimuth at this waypoint
     * @param speed        Approximately how fast the robot should be going by the time it reaches this waypoint
     * @param acceleration Maximum acceleration allowed to reach the target speed
     * @param deceleration Maximum deceleration allowed to reach the target speed
     * @return A waypoint with the specified properties
     * @see <a href="http://mathworld.wolfram.com/SphericalCoordinates.html">Spherical Coordinates</a>
     */
    public static SplinePPWaypoint simple3D(double x, double y, double z, double theta, double phi, double speed, double acceleration, double deceleration) {
        ImmutableVector tanVec = new ImmutableVector(
                kTheta * Math.sin(phi) * Math.cos(theta),
                kTheta * Math.sin(phi) * Math.sin(theta),
                kTheta * Math.cos(phi)
        );
        return new SplinePPWaypoint(new ImmutableVector(x, y, z), tanVec, speed, acceleration, deceleration);
    }

    public ImmutableVector getTanVec() {
        return tanVec;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SplinePPWaypoint{");
        sb.append("location=").append(getLocation());
        sb.append(", speed=").append(getSpeed());
        sb.append(", acceleration=").append(getAcceleration());
        sb.append(", deceleration=").append(getDeceleration());
        sb.append(", tanVec=").append(tanVec);
        sb.append('}');
        return sb.toString();
    }

    public static class Builder {
        private List<SplinePPWaypoint> waypointList = new ArrayList<>();

        @Deprecated
        public Builder add(double x, double y, double xPrime, double yPrime, double speed, double acceleration, double deceleration) {
            SplinePPWaypoint waypoint = new SplinePPWaypoint(new ImmutableVector(x, y), new ImmutableVector(xPrime, yPrime), speed, acceleration, deceleration);
            waypointList.add(waypoint);
            return this;
        }

        /**
         * Add a spline waypoint to the builder
         *
         * @param x
         * @param y
         * @param theta        Radians, rotated such that up is 0, left is Math.PI / 2, etc.
         * @param speed
         * @param acceleration
         * @param deceleration
         * @return
         */
        public Builder add(double x, double y, double theta, double speed, double acceleration, double deceleration) {
            theta += Math.PI / 2;
            SplinePPWaypoint waypoint = SplinePPWaypoint.simple2D(x, y, theta, speed, acceleration, deceleration);
            waypointList.add(waypoint);
            return this;
        }

        public Builder flipY() {
            Builder ret = new Builder();
            for(SplinePPWaypoint wp : waypointList) {
                ret.add(-wp.getLocation().get(0), wp.getLocation().get(1), -wp.getTanVec().get(0), wp.getTanVec().get(1), wp.getSpeed(), wp.getAcceleration(), wp.getDeceleration());
            }
            return ret;
        }

        public PP_PathGenerator buildPathGenerator() {
            return new PP_PathGenerator(QuinticSpline.toPathSegments(buildSplines(), waypointList));
        }

        public List<QuinticSpline> buildSplines() {
            if (waypointList.size() > 1) {
                List<QuinticSpline> splines = new ArrayList<>();
                for (int i = 1; i < waypointList.size(); i++) {
                    SplinePPWaypoint prevWP = waypointList.get(i - 1);
                    SplinePPWaypoint thisWP = waypointList.get(i);
                    ImmutableVector prevWpTanvec = prevWP.tanVec;

                    if (MathUtils.epsilonEquals(prevWpTanvec.mag(), kTheta)) {
                        prevWpTanvec = prevWP.tanVec.mul(prevWP.getLocation().dist(thisWP.getLocation()));
                    }

                    splines.add(new QuinticSpline(prevWP.getLocation(), thisWP.getLocation(), prevWpTanvec, thisWP.tanVec));
                }
                return splines;
            } else {
                throw new IllegalStateException("Cannot create spline with less than 2 waypoints");
            }
        }
    }
}
