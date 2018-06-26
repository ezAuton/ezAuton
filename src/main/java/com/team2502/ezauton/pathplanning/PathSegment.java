package com.team2502.ezauton.pathplanning;

import com.team2502.ezauton.trajectory.geometry.ImmutableVector;
import com.team2502.ezauton.utils.MathUtils;

/**
 * Makes segments created by two {@link ImmutableVector}s easier to work with in {@link Path}
 */
public abstract class PathSegment implements IPathSegment
{
    private final ImmutableVector from;
    private final ImmutableVector to;
    private final boolean finish;
    private final ImmutableVector differenceVec;
    private final boolean beginning;
    private final double distanceStart;
    private final double distanceEnd;
    private final ImmutableVector dPos;
    //    private final double maxSpeed;
    private double length;
//    private MotionProfile motionProfiles;

    protected PathSegment(ImmutableVector from, ImmutableVector to, boolean finish, boolean beginning, double distanceStart)
    {
//        this.maxSpeed = maxSpeed;
        this.finish = finish;
        this.from = from;
        this.to = to;
        differenceVec = to.sub(from);
        this.length = this.from.dist(this.to);
        if(MathUtils.epsilonEquals(0, length))
        {
            throw new IllegalArgumentException("PathSegment length must be non-zero.");
        }
        this.beginning = beginning;
        this.distanceStart = distanceStart;
        this.distanceEnd = distanceStart + length;
        dPos = to.sub(from);
    }

    @Override
    public ImmutableVector getClosestPoint(ImmutableVector robotPos)
    {
        return MathUtils.Geometry.getClosestPointLineSegments(from, to, robotPos);
    }

    @Override
    public double getAbsoluteDistance(ImmutableVector linePos)
    {
        if(to.equals(linePos))
        {
            return distanceEnd;
        }

        if(from.equals(linePos))
        {
            return distanceStart;
        }

        // The difference between from, truncating 0
        ImmutableVector dif = linePos.sub(from);
        for(int i = 0; i < dif.getElements().length; i++)
        {
            double element = dif.get(i);
            if(element != 0)
            {
                double proportion = element / dPos.get(i);
                return proportion * length;
            }
        }
        throw new ArithmeticException("Somehow dif has a dimension of 0.");
    }

    public double getRelativeDistance(double absoluteDistance)
    {
        return absoluteDistance - distanceStart;
    }

    @Override
    public abstract double getSpeed(double absoluteDistance);

    private void checkDistance(double absoluteDistance)
    {
        if(!MathUtils.Algebra.bounded(distanceStart, absoluteDistance, distanceEnd))
        {
            throw new IllegalArgumentException("Must be within bounds");
        }
    }

    public ImmutableVector getPoint(double relativeDistance)
    {
        return dPos.mul(relativeDistance / length).add(from);
    }

    /**
     * Get the distance left squared
     *
     * @param point a close point
     * @return
     * @deprecated
     */
    public double getDistanceLeft2(ImmutableVector point)
    {
        return to.sub(point).mag2();
    }

    /**
     * @return How far along the entire path that the from point is
     */
    public double getAbsoluteDistanceStart()
    {
        return distanceStart;
    }

    /**
     * @return How far along the entire path that the end point is
     */
    public double getAbsoluteDistanceEnd()
    {
        return distanceEnd;
    }

    @Override
    public boolean isBeginning()
    {
        return beginning;
    }

    @Override
    public boolean isFinish()
    {
        return finish;
    }

    public ImmutableVector getFrom()
    {
        return from;
    }

    public ImmutableVector getTo()
    {
        return to;
    }

    public double getLength()
    {
        return length;
    }

    public ImmutableVector getdPos()
    {
        return dPos;
    }

    @Override
    public String toString()
    {
        return "PathSegment{" +
               "from=" + from +
               ", to=" + to +
               '}';
    }


}
