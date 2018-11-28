package org.github.ezauton.ezauton.pathplanning.purepursuit;

import org.github.ezauton.ezauton.pathplanning.IPathSegment;
import org.github.ezauton.ezauton.pathplanning.Path;
import org.github.ezauton.ezauton.trajectory.geometry.ImmutableVector;

/**
 * The main logic behind Pure Pursuit ... returns the subsequent location the robot should try to
 * go towards.
 */
public class PurePursuitMovementStrategy
{
    /**
     * The path that we're driving on
     */
    private final Path path;

    /**
     * How close we need to be to the final waypoint for us to decide that we are finished
     */
    private final double stopTolerance;

    private boolean isFinished = false;
    private ImmutableVector latestClosestPoint;
    private double latestDCP;
    private double latestLookahead;
    private ImmutableVector latestGoalPoint;


    /**
     * Strategize your movement!
     *
     * @param path          The path to drive along
     * @param stopTolerance How close we need to be to the final waypoint for us to decide that we are finished
     */
    public PurePursuitMovementStrategy(Path path, double stopTolerance)
    {
        this.path = path;
        if(stopTolerance <= 0)
        {
            throw new IllegalArgumentException("stopTolerance must be a positive number!");
        }
        this.stopTolerance = stopTolerance;
    }

    /**
     * @return The absolute location of the selected goal point.
     * The goal point is a point on the path 1 lookahead distance away from us.
     * We want to drive at it.
     * @see <a href="https://www.chiefdelphi.com/forums/showthread.php?threadid=162713">Velocity and End Behavior (Chief Delphi)</a>
     */
    private ImmutableVector calculateAbsoluteGoalPoint(double distanceCurrentSegmentLeft, double lookAheadDistance)
    {
        // The intersections with the path we are following and the circle around the robot of
        // radius lookAheadDistance. These intersections will determine the "goal point" we
        // will generate an arc to go to.

        return path.getGoalPoint(distanceCurrentSegmentLeft, lookAheadDistance);
    }


    /**
     * @param loc       Current position of the robot
     * @param lookahead Current lookahead as given by an ILookahead instance
     * @return The wanted pose of the robot at a certain location
     */
    public ImmutableVector update(ImmutableVector loc, double lookahead)
    {
        latestLookahead = lookahead;
        IPathSegment current = path.getCurrent();
        latestClosestPoint = path.getClosestPoint(loc);
        double currentDistance = current.getAbsoluteDistance(latestClosestPoint);
        double distanceLeftSegment = current.getAbsoluteDistanceEnd() - currentDistance;
        latestDCP = latestClosestPoint.dist(loc);

        if(distanceLeftSegment < 0)
        {
            if(path.progressIfNeeded(distanceLeftSegment, latestDCP, loc).size() != 0)
            {
                return update(loc, lookahead);
            }
        }

        double finalDistance = path.getLength();

        double distanceLeftTotal = finalDistance - currentDistance;

        if(distanceLeftTotal < stopTolerance)
        {
            isFinished = true;
//            return null;
        }

        path.progressIfNeeded(distanceLeftSegment, latestDCP, loc);
        latestGoalPoint = calculateAbsoluteGoalPoint(distanceLeftSegment, lookahead);
        return latestGoalPoint;
    }

    public Path getPath()
    {
        return path;
    }

    public boolean isFinished()
    {
        return isFinished;
    }

    public double getLatestLookahead()
    {
        return latestLookahead;
    }

    public ImmutableVector getClosestPoint()
    {
        return latestClosestPoint;
    }

    public ImmutableVector getGoalPoint()
    {
        return latestGoalPoint;
    }

    public double getDCP()
    {
        return latestDCP;
    }
}
