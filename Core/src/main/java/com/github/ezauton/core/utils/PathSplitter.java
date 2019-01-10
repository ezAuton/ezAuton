package com.github.ezauton.core.utils;

import com.github.ezauton.core.pathplanning.IPathSegment;
import com.github.ezauton.core.pathplanning.LinearPathSegment;
import com.github.ezauton.core.trajectory.geometry.ImmutableVector;

import java.util.ArrayList;
import java.util.List;

/**
 * A
 */
public class PathSplitter
{

    private final IPathSegment pathSegment;
    private final double ds;

    public PathSplitter(IPathSegment pathSegment, double ds)
    {
        this.pathSegment = pathSegment;
        this.ds = ds;
    }

    /**
     * Note: used purely for data. There is no interpolation in these values and all data is stored.
     *
     * @return
     */
    List<IPathSegment> calculateSegments()
    {
        List<IPathSegment> pathSegments = new ArrayList<>();

        ImmutableVector from = pathSegment.getFrom();

        ImmutableVector normSlope = from.sub(pathSegment.getTo()).norm();
        ImmutableVector dVec = normSlope.mul(ds);

        double lengthOn = 0;

        double lengthTotal = pathSegment.getLength();
        while(lengthOn < lengthTotal)
        {
            double lengthLeft = lengthTotal - lengthOn;

            ImmutableVector to;
            boolean end = lengthLeft <= ds;
            if(end)
            {
                to = from.add(normSlope.mul(lengthLeft));
            }
            else
            {
                to = from.add(dVec);
            }

            boolean beginning = pathSegment.isBeginning() && lengthOn == 0;
            boolean finish = pathSegment.isFinish() && end;

            double distanceStart = pathSegment.getAbsoluteDistanceStart() + lengthOn;

            IPathSegment ps = new LinearPathSegment(from, to, finish, beginning, distanceStart)
            {
                private double avgSpeed;

                {
                    double minSpeed = pathSegment.getSpeed(getAbsoluteDistanceStart());
                    double maxSpeed = pathSegment.getSpeed(getAbsoluteDistanceEnd());
                    avgSpeed = (minSpeed + maxSpeed) / 2D;
                }

                @Override
                public double getSpeed(double absoluteDistance)
                {
                    return avgSpeed;
                }
            };
            lengthOn += ds;
            from = to;
            pathSegments.add(pathSegment);
        }

        return pathSegments;

    }
}
