package com.github.ezauton.core.helper;

import com.github.ezauton.core.pathplanning.PP_PathGenerator;
import com.github.ezauton.core.pathplanning.Path;
import com.github.ezauton.core.pathplanning.purepursuit.PPWaypoint;

/**
 * Class containing sample paths for Pure Pursuit
 * <p>
 * This class only contains static members.
 */
public class PathHelper
{
    /**
     * Describes a path 12 (ft? m?) long reaching a max velocity of 5 (ft/s? m/s?) with a max accel and decel of 3 (ft/s^2? m/s^2)
     */
    public static Path STRAIGHT_12UNITS;

    static
    {
        PPWaypoint waypoint1 = PPWaypoint.simple2D(0, 0, 0, 3, -3);
        PPWaypoint waypoint2 = PPWaypoint.simple2D(0, 6, 5, 3, -3);
        PPWaypoint waypoint3 = PPWaypoint.simple2D(0, 12, 0, 3, -3);

        PP_PathGenerator pathGenerator = new PP_PathGenerator(waypoint1, waypoint2, waypoint3);
        STRAIGHT_12UNITS = pathGenerator.generate(0.05);
    }
}
