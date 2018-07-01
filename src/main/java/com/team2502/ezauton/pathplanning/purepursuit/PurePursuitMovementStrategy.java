package com.team2502.ezauton.pathplanning.purepursuit;

import com.team2502.ezauton.pathplanning.IPathSegment;
import com.team2502.ezauton.pathplanning.Path;
import com.team2502.ezauton.trajectory.geometry.ImmutableVector;

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
    private final double stopTolerance;
    private boolean isFinished = false;


    /**
     * Strategize your movement!
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
     * @param loc
     * @return The wanted pose of the robot at a certain location
     */
    public ImmutableVector update(ImmutableVector loc, double lookahead)
    {

        IPathSegment current = path.getCurrent();
        ImmutableVector closestPoint = path.getClosestPoint(loc);
        double currentDistance = current.getAbsoluteDistance(closestPoint);
        double distanceLeftSegment = current.getAbsoluteDistanceEnd() - currentDistance;
        double cpDist = closestPoint.dist(loc);
        if(distanceLeftSegment < 0)
        {
            if(path.progressIfNeeded(distanceLeftSegment, cpDist, loc).size() != 0)
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

        path.progressIfNeeded(distanceLeftSegment, cpDist, loc);
        return calculateAbsoluteGoalPoint(distanceLeftSegment, lookahead);
    }

    public Path getPath()
    {
        return path;
    }

    public boolean isFinished()
    {
        return isFinished;
    }
}
