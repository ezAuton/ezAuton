package com.team2502.ezauton.pathplanning;

import com.team2502.ezauton.trajectory.geometry.ImmutableVector;
import com.team2502.ezauton.utils.MathUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * A path is the conglomerate of several {@link IPathSegment}s, which are in turn made from two {@link ImmutableVector}s.
 * Thus, a Path is the overall Path that the robot will take formed by Waypoints.
 * This class is very helpful when it comes to tracking which segment is currently on and getting the distance
 * on the path at any point (taking arclength ... basically making path 1D).
 */
public class Path
{
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
        path.pathSegments = new ArrayList<>(pathSegments);
        IPathSegment last = pathSegments.get(pathSegments.size() - 1);
        path.length = last.getAbsoluteDistanceEnd();
        path.moveNextSegment();
        return path;
    }

    public static Path fromSegments(IPathSegment... pathSegments)
    {
        return fromSegments(Arrays.asList(pathSegments));
    }

    /**
     * @return the total arclength of this path
     */
    public double getLength()
    {
        return length;
    }

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
}
