package com.team2502.ezauton.pathplanning;

import com.team2502.ezauton.pathplanning.purepursuit.PPWaypoint;

import java.util.ArrayList;
import java.util.List;

public class PP_PathGenerator
{

    private final PPWaypoint[] ppWaypoints;

    public PP_PathGenerator(PPWaypoint... ppWaypoints)
    {
        this.ppWaypoints = ppWaypoints;
    }

    public Path generate(double dt)
    {
        List<IPathSegment> pathSegments = new ArrayList<>();
        double distance = 0;
        for(int i = 0; i < ppWaypoints.length - 1; i++)
        {
            PPWaypoint from = ppWaypoints[i];
            PPWaypoint to = ppWaypoints[i + 1];

            //TODO: Update from RobotCode2018 style pathsegments
            LinearPathSegment pathSegment;
            if(i == 0)
            {
                pathSegment = new LinearPathSegment(from.getLocation(), to.getLocation(),
                                                    i == ppWaypoints.length - 2, true, distance)
                {
                    @Override
                    public double getSpeed(double absoluteDistance)
                    {
                        return from.getSpeed() == 0 ? to.getSpeed() : from.getSpeed();
                    }
                };
            }
            else
            {
                pathSegment = new PathSegmentExtrapolated(
                        from.getLocation(), to.getLocation(), i == ppWaypoints.length - 2, false, distance,
                        from.getSpeed(), to.getSpeed(), dt,
                        from.getAcceleration(), from.getDeceleration());
            }
            distance += pathSegment.getLength();
            pathSegments.add(pathSegment);
        }
        return Path.fromSegments(pathSegments);
    }
}
