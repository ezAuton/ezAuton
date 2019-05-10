package com.github.ezauton.core.pathplanning;

import com.github.ezauton.core.trajectory.geometry.ImmutableVector;

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
public class Path {
    private List<PathSegment> pathSegments;

    private int segmentOnI = -1;
    private PathSegment segmentOn;
    private ImmutableVector closestPoint;
    private ImmutableVector robotLocationClosestPoint;
    private double length;

    private Path() {
    }

    /**
     * Create a path from multiple path segments
     *
     * @param pathSegments A List of IPathSegments
     * @return A path consisting of these segments
     */
    public static Path fromSegments(List<PathSegment> pathSegments) {
        if (pathSegments.size() == 0) throw new IllegalArgumentException("Path must have at least one segment");

        Path path = new Path();
        path.pathSegments = new ArrayList<>(pathSegments);
        PathSegment last = pathSegments.get(pathSegments.size() - 1);
        path.length = last.getAbsoluteDistanceEnd();
        path.moveNextSegment();
        return path;
    }

    public static Path fromSegments(PathSegment... pathSegments) {
        return fromSegments(Arrays.asList(pathSegments));
    }

    /**
     * @return the total arclength of this path
     */
    public double getLength() {
        return length;
    }

    /**
     * Moves to the next path segment
     *
     * @return If there was a next path segment to progress to
     */
    public boolean moveNextSegment() {
        if (segmentOnI < pathSegments.size() - 1) {
            segmentOnI++;
            segmentOn = pathSegments.get(segmentOnI);
            return true;
        }
        return false;
    }

    public boolean exists() {
        return !pathSegments.isEmpty();
    }

    public ImmutableVector getClosestPoint(ImmutableVector origin) // TODO: it might be better to not look purely at the current pathsegment and instead previous path segments
    {

        // Commented out because if the PATH changes, this will not give the right result, even if the LOCATION is the same.
        // TODO: figure out a way to put some type of cache back in
//        if(this.robotLocationClosestPoint != null && MathUtils.epsilonEquals(this.robotLocationClosestPoint, origin))
//        {
        // ISSUE what if the path changed during this time!!!!!!!!! :o
//            return closestPoint;
//        }

        this.robotLocationClosestPoint = origin;
        PathSegment current = getCurrent();
        closestPoint = current.getClosestPoint(origin);
        return closestPoint;
    }

    public int getSegmentOnI() {
        return segmentOnI;
    }

    /**
     * Calculate the goal point that we should be driving at
     *
     * @param distanceLeftCurrentSegment The distance left before we complete our segment
     * @param lookahead                  Our current lookahead distance
     * @return Where we should drive at
     */
    public ImmutableVector getGoalPoint(double distanceLeftCurrentSegment, double lookahead) {
        PathSegment current = getCurrent();
        // If our circle intersects on the assertSameDim path
        if (lookahead < distanceLeftCurrentSegment || current.isFinish()) {
            double relativeDistance = current.getLength() - distanceLeftCurrentSegment + lookahead;
            return current.getPoint(relativeDistance);
        }
        // If our circle intersects other segments
        else {
            lookahead -= distanceLeftCurrentSegment;

            for (int i = segmentOnI + 1; i < pathSegments.size(); i++) {
                PathSegment pathSegment = pathSegments.get(i);
                double length = pathSegment.getLength();
                if (lookahead > length && !pathSegment.isFinish()) {
                    lookahead -= length;
                } else {
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
    public List<PathSegment> progressIfNeeded(double distanceLeftSegment, double closestPointDist, ImmutableVector robotPos) {
        //TODO: Move magic number
        // Move to the next segment if we are near the end of the current segment
        if (distanceLeftSegment < .16F) {
            if (moveNextSegment()) {
                return Collections.singletonList(pathSegments.get(segmentOnI - 1));
            }
        }

        // For all paths 2 feet ahead of us, progress on the path if we can
        //TODO: Move magic number
        List<PathSegment> pathSegments = nextSegmentsInclusive(2);
        int i = segmentOnI;
        int j = 0;
        for (PathSegment pathSegment : pathSegments) {
            if (shouldProgress(pathSegment, robotPos, closestPointDist)) {
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
    public void moveSegment(int segmentOnI, PathSegment segmentOn) {
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
    public boolean shouldProgress(PathSegment segment, ImmutableVector robotPos, double currentSegmentCPDist) {
        if (segment == null) // we are on the last segment... we cannot progress
        {
            return false;
        }

        ImmutableVector closestPoint = segment.getClosestPoint(robotPos);
        double nextClosestPointDistance = closestPoint.dist(robotPos);
        // TODO: Move magic number
        return currentSegmentCPDist > nextClosestPointDistance + 0.5F;
    }


    public double getAbsDistanceOfClosestPoint(ImmutableVector closestPoint) {
        PathSegment current = getCurrent();
        ImmutableVector firstLocation = current.getFrom();
        return current.getAbsoluteDistanceStart() + firstLocation.dist(closestPoint);
    }

    /**
     * @param maxAheadDistance The distance to look ahead from the last segment
     * @return The segments that lay on the path between our current position and maxAheadDistance from our current position. This result includes the current path segment.
     */
    public List<PathSegment> nextSegmentsInclusive(double maxAheadDistance) {
        List<PathSegment> segments = new ArrayList<>();
        PathSegment startSegment = getCurrent();
        segments.add(startSegment);
        double distanceStart = startSegment.getAbsoluteDistanceEnd();
        for (int i = segmentOnI + 1; i < pathSegments.size(); i++) {
            PathSegment pathSegment = pathSegments.get(i);
            if (pathSegment.getAbsoluteDistanceStart() - distanceStart < maxAheadDistance) {
                segments.add(pathSegment);
            } else {
                break;
            }
        }
        return segments;
    }

    public PathSegment getCurrent() {
        return segmentOn;
    }

    public PathSegment getNext() {
        int nextSegmentI = segmentOnI + 1;
        if (nextSegmentI >= pathSegments.size()) {
            return null;
        }
        PathSegment nextSegment = pathSegments.get(nextSegmentI);
        return nextSegment;
    }

    public ImmutableVector getStart() {
        return pathSegments.get(0).getFrom();
    }

    public ImmutableVector getEnd() {
        return pathSegments.get(pathSegments.size() - 1).getTo();
    }

    public List<PathSegment> getPathSegments() {
        return pathSegments;
    }

    @Override
    public Path clone() {
        Path path = new Path();
        path.pathSegments = pathSegments;
        path.segmentOn = segmentOn;
        path.segmentOnI = segmentOnI;
        path.closestPoint = closestPoint;
        path.robotLocationClosestPoint = robotLocationClosestPoint;
        return path;
    }
}
