package com.team2502.ezauton.helper;

import com.team2502.ezauton.pathplanning.PP_PathGenerator;
import com.team2502.ezauton.pathplanning.Path;
import com.team2502.ezauton.pathplanning.purepursuit.PPWaypoint;

/**
 * Class containing sample paths for Pure Pursuit
 * <p>
 * This class only contains static members.
 */
public class Paths
{
    /**
     * Describes a path 12 (ft? m?) long reaching a max velocity of 5 (ft/s? m/s?) with a max accel and decel of 3 (ft/s^2? m/s^2)
     */
    public static Path STRAIGHT_12FT;

    static
    {
        PPWaypoint waypoint1 = PPWaypoint.simple2D(0, 0, 0, 3, -3);
        PPWaypoint waypoint2 = PPWaypoint.simple2D(0, 6, 5, 3, -3);
        PPWaypoint waypoint3 = PPWaypoint.simple2D(0, 12, 0, 3, -3);

        PP_PathGenerator pathGenerator = new PP_PathGenerator(waypoint1, waypoint2, waypoint3);
        STRAIGHT_12FT = pathGenerator.generate(0.05);
    }
}
