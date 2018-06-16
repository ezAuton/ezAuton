package com.team2502.ezauton.pathplanning;

import com.team2502.ezauton.trajectory.geometry.ImmutableVector;
import com.team2502.ezauton.utils.MathUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A path is the conglomerate of several {@link PathSegment}s, which are in turn made from two {@link ImmutableVector}s.
 * Thus, a Path is the overall Path that the robot will take formed by Waypoints.
 * This class is very helpful when it comes to tracking which segment is currently on and getting the distance
 * on the path at any point (taking arclength ... basically making path 1D).
 */
public class Path
{

    private static final double SEGMENTS_PER_UNIT = 2; // 2 segments per foot -> 6 inches per segment. Pretty reasonable resolution for a 2 foot long robot.
    private List<PathSegment> pathSegments;

    private int segmentOnI = -1;
    private PathSegment segmentOn;
    private ImmutableVector closestPoint;
    private ImmutableVector robotLocationClosestPoint;

    private Path() {}

    public static Path fromSegments(List<PathSegment> pathSegments)
    {
        Path path = new Path();
        path.pathSegments = pathSegments;
        path.moveNextSegment();
        return path;
    }

//    public static Path fromPoints(List<? extends ImmutableVector> waypointList)
//    {
//        List<PathSegment> pathSegments = new ArrayList<>();
//        float distance = 0;
//        for(int i = 0; i < waypointList.size() - 1; i++)
//        {
//            ImmutableVector waypoint1 = waypointList.get(i);
//            ImmutableVector waypoint2 = waypointList.get(i + 1);
//            double length = waypoint1.dist(waypoint2);
//            PathSegment pathSegment = new PathSegment(waypoint1, waypoint2, i == 0, i == waypointList.size() - 2, distance, distance += length, length);
//            pathSegments.add(pathSegment);
//        }
//        return fromSegments(pathSegments);
//    }

//    public static Path fromSplinePoints(List<SplineWaypoint> waypointList)
//    {
//        List<Waypoint> interpolatedWaypoints = new ArrayList<>();
//        float distance = 0;
//        for(int i = 0; i < waypointList.size() - 1; i++)
//        {
//            SplineWaypoint waypoint1 = waypointList.get(i);
//            Point waypoint1Slope = waypointList.get(i).getSlopeVec();
//
//            SplineWaypoint waypoint2 = waypointList.get(i + 1);
//            Point waypoint2Slope = waypointList.get(i + 1).getSlopeVec();
//
//            float length = (float) SplinePathSegment.getArcLength(waypoint1, waypoint2, waypoint1Slope, waypoint2Slope, 0, 1);
//
//            SplinePathSegment pathSegment = new SplinePathSegment(waypoint1, waypoint2, waypoint1Slope, waypoint2Slope,i == 0, i == waypointList.size() - 2, distance, distance += length, length);
//            int interpolatedSegNum = (int) (SEGMENTS_PER_UNIT * pathSegment.getLength());
//
//            InterpolationMap maxVel = new InterpolationMap(0D, (double) waypoint1.getMaxVelocity());
//            final float maxSpeedWaypoint2 = waypoint2.getMaxVelocity();
//            if(maxSpeedWaypoint2 < 0)
//            {
//                throw new IllegalArgumentException("Somehow, maxSpeed is less than 0 for this waypoint: " + waypoint2.toString());
//            }
//            maxVel.put(1D, (double) maxSpeedWaypoint2);
//
//            InterpolationMap maxAccel = new InterpolationMap(0D, (double) waypoint1.getMaxAccel());
//            maxAccel.put(1D, (double) waypoint2.getMaxAccel());
//
//            InterpolationMap maxDecel = new InterpolationMap(0D, (double) waypoint1.getMaxDeccel());
//            maxDecel.put(1D, (double) waypoint2.getMaxDeccel());
//
//            for(int j = 0; j < interpolatedSegNum; j++)
//            {
//                double t = (double) j / interpolatedSegNum;
//                ImmutableVector2f loc = pathSegment.get(t);
//                final float maxSpeed = maxVel.get(t).floatValue();
//                if(maxSpeed < 0)
//                {
//                    throw new IllegalArgumentException("Max speed is negative!");
//                }
//                Waypoint waypoint = new Waypoint(loc, maxSpeed, maxAccel.get(t).floatValue(), maxDecel.get(t).floatValue(), j == 0 ? waypoint1.getCommands() : null);
//                interpolatedWaypoints.add(waypoint);
//            }
//        }
//        return fromPoints(interpolatedWaypoints);
//    }

//    public static Path fromPoints(ImmutableVector... points)
//    {
//        return fromPoints(Arrays.asList(points));
//    }

//    public static Path fromSplinePoints(SplineWaypoint... points)
//    {
//        return fromSplinePoints(Arrays.asList(points));
//    }

    /**
     * @return If there are more segments
     */
    public boolean moveNextSegment()
    {
        if(segmentOnI < pathSegments.size() - 1)
        {
            segmentOnI++;
            segmentOn = pathSegments.get(segmentOnI);
            return true;
        }
        return false;
    }

    public boolean exists()
    {
        return !pathSegments.isEmpty();
    }

    public ImmutableVector getClosestPoint(ImmutableVector origin) // TODO: it might be better to not look purely at the current pathsegment and instead previous path segments
    {
        if(this.robotLocationClosestPoint != null && MathUtils.epsilonEquals(this.robotLocationClosestPoint, origin))
        {
            return closestPoint;
        }

        this.robotLocationClosestPoint = origin;
        PathSegment current = getCurrent();
        closestPoint = current.getClosestPoint(origin);
        return closestPoint;
    }

    public int getSegmentOnI()
    {
        return segmentOnI;
    }

    /**
     * Calculate the goal point that we should be driving at
     *
     * @param distanceLeftCurrentSegment The distance left before we complete our segment
     * @param lookahead                  Our current lookahead distance
     * @return Where we should drive at
     */
    public ImmutableVector getGoalPoint(double distanceLeftCurrentSegment, double lookahead)
    {
        PathSegment current = getCurrent();
        // If our circle intersects on the assertSameDim path
        if(lookahead < distanceLeftCurrentSegment || current.isFinish())
        {
            double relativeDistance = current.getLength() - distanceLeftCurrentSegment + lookahead;
            return current.getPoint(relativeDistance);
        }
        // If our circle intersects other segments
        else
        {
            lookahead -= distanceLeftCurrentSegment;

            for(int i = segmentOnI + 1; i < pathSegments.size(); i++)
            {
                PathSegment pathSegment = pathSegments.get(i);
                double length = pathSegment.getLength();
                if(lookahead > length && !pathSegment.isFinish())
                {
                    lookahead -= length;
                }
                else
                {
                    return pathSegment.getPoint(lookahead);
                }
            }
        }
        return null;
    }

    /**
     *
     * @param distanceLeftSegment
     * @param closestPointDist
     * @param robotPos
     * @return The PathSegments progressed
     */
    public List<PathSegment> progressIfNeeded(float distanceLeftSegment, float closestPointDist, ImmutableVector robotPos)
    {

        if(distanceLeftSegment < 0)
        {
            throw new IllegalArgumentException("distanceLeftSegment cannot be less than 0!");
        }

        // path segments 2 ft ahead
        List<PathSegment> pathSegments = nextSegmentsInclusive(2);
        int i = segmentOnI;
        int j = 0;
        for(PathSegment pathSegment : pathSegments)
        {
            if(shouldProgress(pathSegment,robotPos,closestPointDist))
            {
                moveSegment(i,pathSegment);
                return pathSegments.subList(0,j+1);
            }
            i++;
            j++;
        }
        return Collections.emptyList();
    }

    public void moveSegment(int segmentOnI, PathSegment segmentOn)
    {
        this.segmentOnI = segmentOnI;
        this.segmentOn = segmentOn;
    }

    public boolean shouldProgress(PathSegment segment, ImmutableVector robotPos, float currentSegmentCPDist)
    {
        if(segment == null) // we are on the last segment... we cannot progress
        {
            return false;
        }

        ImmutableVector closestPoint = segment.getClosestPoint(robotPos);
        double nextClosestPointDistance = closestPoint.dist(robotPos);
        // TODO: add 0.5 as constant
        return currentSegmentCPDist > nextClosestPointDistance + 0.5F;
    }


    public double getAbsDistanceOfClosestPoint(ImmutableVector closestPoint)
    {
        PathSegment current = getCurrent();
        ImmutableVector firstLocation = current.getFrom();
        return current.getAbsoluteDistanceStart() + firstLocation.dist(closestPoint);
    }

    /**
     * @param maxAheadDistance The distance to look ahead from the last segment
     * @return
     */
    public List<PathSegment> nextSegmentsInclusive(float maxAheadDistance)
    {
        List<PathSegment> segments = new ArrayList<>();
        PathSegment startSegment = getCurrent();
        segments.add(startSegment);
        double distanceStart = startSegment.getAbsoluteDistanceEnd();
        for(int i = segmentOnI + 1; i < pathSegments.size(); i++)
        {
            PathSegment pathSegment = pathSegments.get(i);
            if(pathSegment.getAbsoluteDistanceStart() - distanceStart < maxAheadDistance)
            {
                segments.add(pathSegment);
            }
            else
            {
                break;
            }
        }
        return segments;
    }

    public PathSegment getCurrent()
    {
        return segmentOn;
    }

    public PathSegment getNext()
    {
        int nextSegmentI = segmentOnI + 1;
        if(nextSegmentI >= pathSegments.size())
        {
            return null;
        }
        PathSegment nextSegment = pathSegments.get(nextSegmentI);
        return nextSegment;
    }

    public ImmutableVector getStart()
    {
        return pathSegments.get(0).getFrom();
    }

    public ImmutableVector getEnd()
    {
        return pathSegments.get(pathSegments.size() - 1).getTo();
    }

    public List<PathSegment> getPathSegments()
    {
        return pathSegments;
    }

    @Override
    public Path clone()
    {
        Path path = new Path();
        path.pathSegments = pathSegments;
        path.segmentOn = segmentOn;
        path.segmentOnI = segmentOnI;
        path.closestPoint = closestPoint;
        path.robotLocationClosestPoint = robotLocationClosestPoint;
        return path;
    }

//    public List<Waypoint> getWaypoints()
//    {
//        List<Waypoint> ret = new ArrayList<>();
//        for(PathSegment segment : pathSegments)
//        {
//            if(segment.getFrom().getClass().equals(Waypoint.class))
//            {
//                ret.add((Waypoint) segment.getFrom());
//            }
//        }
//
//        if(pathSegments.get(pathSegments.size() - 1).getTo().getClass().equals(Waypoint.class))
//        {
//            ret.add((Waypoint) pathSegments.get(pathSegments.size() - 1).getTo());
//        }
//        return ret;
//    }
}
