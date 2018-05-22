package com.team2502.ezauton.pathplanning;

import com.team2502.ezauton.trajectory.geometry.ImmutableVector;
import com.team2502.ezauton.utils.MathUtils;

/**
 * Makes segments created by two {@link ImmutableVector}s easier to work with in {@link Path}
 */
public class PathSegment
{
    private final ImmutableVector first;
    private final ImmutableVector last;
    protected double length;
    private final boolean end;
    private final boolean start;
    private final float distanceStart;
    private final double distanceEnd;
    private final ImmutableVector dPos;

    protected PathSegment(ImmutableVector first, ImmutableVector last, boolean start, boolean end, float distanceStart, double distanceEnd, double length)
    {
        this.first = first;
        this.last = last;
        this.length = length;
        this.end = end;
        this.start = start;
        this.distanceStart = distanceStart;
        this.distanceEnd = distanceEnd;
        dPos = last.sub(first);
    }

    public ImmutableVector getClosestPoint(ImmutableVector robotPos)
    {
        return MathUtils.Geometry.getClosestPointLineSegments(first, last, robotPos);
    }

    public ImmutableVector getPoint(double relativeDistance)
    {
        return dPos.mul(relativeDistance / length).add(first);
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
        ImmutableVector lastLocation = last;
        return lastLocation.sub(point).mag2();
    }

    /**
     * Get the distance left
     *
     * @param point a point on the line
     * @return
     */
    public double getDistanceLeft(ImmutableVector point)
    {
        return last.dist2(point);
    }

    /**
     * @return How far along the entire path that the first point is
     */
    public float getAbsoluteDistanceStart()
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

    /**
     * @return If this segment is the last segment in the path
     */
    public boolean isEnd()
    {
        return end;
    }

    /**
     * @return If this segment is the first segment in the path
     */
    public boolean isStart()
    {
        return start;
    }

    public ImmutableVector getFirst()
    {
        return first;
    }

    public ImmutableVector getLast()
    {
        return last;
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
               "first=" + first +
               ", last=" + last +
               '}';
    }
}
