package com.github.ezauton.core.pathplanning;

import com.github.ezauton.core.pathplanning.purepursuit.PPWaypoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates a path 🗺 for Pure Pursuit given {@link PPWaypoint}
 */
public class PP_PathGenerator implements Serializable
{

    private final PPWaypoint[] ppWaypoints;

    public PP_PathGenerator(PPWaypoint... ppWaypoints)
    {
        this.ppWaypoints = ppWaypoints;
    }

    public Path generate(double dt)
    {
        List<IPathSegment> pathSegments = new ArrayList<>();
        double addedDistance = 0;
        for(int i = 0; i < ppWaypoints.length - 1; i++)
        {
            PPWaypoint from = ppWaypoints[i];
            PPWaypoint to = ppWaypoints[i + 1];

            //TODO: Update from RobotCode2018 style pathsegments
            LinearPathSegment pathSegment;
            if(i == 0)
            {
                double beginningSpeed = from.getSpeed() == 0 ? to.getSpeed() : from.getSpeed();


                pathSegment = new PathSegmentInterpolated(
                        from.getLocation(), to.getLocation(), i == ppWaypoints.length - 2, true, addedDistance,
                        beginningSpeed, to.getSpeed(), dt,
                        from.getAcceleration(), from.getDeceleration());
            }
            else
            {
                pathSegment = new PathSegmentInterpolated(
                        from.getLocation(), to.getLocation(), i == ppWaypoints.length - 2, false, addedDistance,
                        from.getSpeed(), to.getSpeed(), dt,
                        from.getAcceleration(), from.getDeceleration());
            }
            addedDistance += pathSegment.getLength();
            pathSegments.add(pathSegment);
        }
        return Path.fromSegments(pathSegments);
    }
}
