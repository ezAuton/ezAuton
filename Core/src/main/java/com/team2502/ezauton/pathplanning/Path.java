package com.team2502.ezauton.pathplanning;

import com.team2502.ezauton.trajectory.geometry.ImmutableVector;
import com.team2502.ezauton.utils.MathUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A path is the conglomerate of several {@link IPathSegment}s, which are in turn made from two {@link ImmutableVector}s.
 * Thus, a Path is the overall Path that the robot will take formed by Waypoints.
 * This class is very helpful when it comes to tracking which segment is currently on and getting the distance
 * on the path at any point (taking arclength ... basically making path 1D).
 */
public class Path
{

    private static final double SEGMENTS_PER_UNIT = 2; // 2 segments per foot -> 6 inches per segment. Pretty reasonable resolution for a 2 foot long robot.
    private List<IPathSegment> pathSegments;

    private int segmentOnI = -1;
    private IPathSegment segmentOn;
    private ImmutableVector closestPoint;
    private ImmutableVector robotLocationClosestPoint;
    private double length;

    private Path() {}

    /**
     * Create a path from multiple path segments
     *
     * @param pathSegments A List of IPathSegments
     * @return A path consisting of these segments
     */
    public static Path fromSegments(List<IPathSegment> pathSegments)
    {
        Path path = new Path();
        path.pathSegments = pathSegments;
        IPathSegment last = pathSegments.get(pathSegments.size() - 1);
        path.length = last.getAbsoluteDistanceEnd();
        path.moveNextSegment();
        return path;
    }

    /**
     * @return the total arclength of this path
     */
    public double getLength()
    {
        return length;
    }

    //TODO: Splines?
    //    public static Path fromPoints(List<? extends ImmutableVector> waypointList)
//    {
//        List<PathSegment> pathSegments = new ArrayList<>();
//        double distance = 0;
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
//        double distance = 0;
//        for(int i = 0; i < waypointList.size() - 1; i++)
//        {
//            SplineWaypoint waypoint1 = waypointList.get(i);
//            Point waypoint1Slope = waypointList.get(i).getSlopeVec();
//
//            SplineWaypoint waypoint2 = waypointList.get(i + 1);
//            Point waypoint2Slope = waypointList.get(i + 1).getSlopeVec();
//
//            double length = (double) SplinePathSegment.getArcLength(waypoint1, waypoint2, waypoint1Slope, waypoint2Slope, 0, 1);
//
//            SplinePathSegment pathSegment = new SplinePathSegment(waypoint1, waypoint2, waypoint1Slope, waypoint2Slope,i == 0, i == waypointList.size() - 2, distance, distance += length, length);
//            int interpolatedSegNum = (int) (SEGMENTS_PER_UNIT * pathSegment.getLength());
//
//            InterpolationMap maxVel = new InterpolationMap(0D, (double) waypoint1.getMaxVelocity());
//            final double maxSpeedWaypoint2 = waypoint2.getMaxVelocity();
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
//                final double maxSpeed = maxVel.get(t).doubleValue();
//                if(maxSpeed < 0)
//                {
//                    throw new IllegalArgumentException("Max speed is negative!");
//                }
//                Waypoint waypoint = new Waypoint(loc, maxSpeed, maxAccel.get(t).doubleValue(), maxDecel.get(t).doubleValue(), j == 0 ? waypoint1.getCommands() : null);
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
     * Moves to the next path segment
     *
     * @return If there was a next path segment to progress to
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
        IPathSegment current = getCurrent();
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
        IPathSegment current = getCurrent();
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
                IPathSegment pathSegment = pathSegments.get(i);
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


    //TODO: make this better

    /**
     * @param distanceLeftSegment The distance left before we are on the next path segment
     * @param closestPointDist    The distance to the closest point on the current path segment
     * @param robotPos            The location of the robot
     * @return The PathSegments that have been progressed
     */
    public List<IPathSegment> progressIfNeeded(double distanceLeftSegment, double closestPointDist, ImmutableVector robotPos)
    {
        //TODO: Move magic number
        // Move to the next segment if we are near the end of the current segment
        if(distanceLeftSegment < .16F)
        {
            if(moveNextSegment())
            {
                return Collections.singletonList(pathSegments.get(segmentOnI - 1));
            }
        }

        // For all paths 2 feet ahead of us, progress on the path if we can
        //TODO: Move magic number
        List<IPathSegment> pathSegments = nextSegmentsInclusive(2);
        int i = segmentOnI;
        int j = 0;
        for(IPathSegment pathSegment : pathSegments)
        {
            if(shouldProgress(pathSegment, robotPos, closestPointDist))
            {
                moveSegment(i, pathSegment);
                return pathSegments.subList(0, j + 1);
            }
            i++;
            j++;
        }
        return Collections.emptyList();
    }

    /**
     * Move to another path segment
     *
     * @param segmentOnI The index of the path segment
     * @param segmentOn  The instance of the path segment
     */
    public void moveSegment(int segmentOnI, IPathSegment segmentOn)
    {
        this.segmentOnI = segmentOnI;
        this.segmentOn = segmentOn;
    }

    /**
     * Check if we should progress to another segment
     *
     * @param segment              The instance of this "other" segment
     * @param robotPos             The position of the robot
     * @param currentSegmentCPDist The distance to the closest point on the current segment
     * @return If we should progress to this "other" path segment
     */
    public boolean shouldProgress(IPathSegment segment, ImmutableVector robotPos, double currentSegmentCPDist)
    {
        if(segment == null) // we are on the last segment... we cannot progress
        {
            return false;
        }

        ImmutableVector closestPoint = segment.getClosestPoint(robotPos);
        double nextClosestPointDistance = closestPoint.dist(robotPos);
        // TODO: Move magic number
        return currentSegmentCPDist > nextClosestPointDistance + 0.5F;
    }


    public double getAbsDistanceOfClosestPoint(ImmutableVector closestPoint)
    {
        IPathSegment current = getCurrent();
        ImmutableVector firstLocation = current.getFrom();
        return current.getAbsoluteDistanceStart() + firstLocation.dist(closestPoint);
    }

    /**
     * @param maxAheadDistance The distance to look ahead from the last segment
     * @return The segments that lay on the path between our current position and maxAheadDistance from our current position. This result includes the current path segment.
     */
    public List<IPathSegment> nextSegmentsInclusive(double maxAheadDistance)
    {
        List<IPathSegment> segments = new ArrayList<>();
        IPathSegment startSegment = getCurrent();
        segments.add(startSegment);
        double distanceStart = startSegment.getAbsoluteDistanceEnd();
        for(int i = segmentOnI + 1; i < pathSegments.size(); i++)
        {
            IPathSegment pathSegment = pathSegments.get(i);
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

    public IPathSegment getCurrent()
    {
        return segmentOn;
    }

    public IPathSegment getNext()
    {
        int nextSegmentI = segmentOnI + 1;
        if(nextSegmentI >= pathSegments.size())
        {
            return null;
        }
        IPathSegment nextSegment = pathSegments.get(nextSegmentI);
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

    public List<IPathSegment> getPathSegments()
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
