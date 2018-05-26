package com.team2502.ezauton.pathplanning.purepursuit;

import com.team2502.ezauton.pathplanning.Path;
import com.team2502.ezauton.trajectory.geometry.ImmutableVector;

import java.util.List;

/**
 * The main logic behind Pure Pursuit
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
     *
     * @param waypoints A list of waypoints for the robot to drive through
     */
    public PurePursuitMovementStrategy(List<ImmutableVector> waypoints, double stopTolerance)
    {
        ImmutableVector.assertSameDim(waypoints);
        this.path = Path.fromPoints(waypoints);
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
     *
     * @param pose
     * @param closestPoint
     * @param lookahead
     * @return The wanted pose of the robot at a certain location
     */
    public ImmutableVector update(ImmutableVector pose, ImmutableVector closestPoint, double lookahead)
    {

        if(pose == null)
        {
            throw new IllegalArgumentException("Pose cannot be null");
        }

        double distanceLeftSq = path.getCurrent().getDistanceLeft(closestPoint);

        if(path.getCurrent().isEnd() && distanceLeftSq < stopTolerance)
        {
            isFinished = true;
            return null;
        }
        return calculateAbsoluteGoalPoint(distanceLeftSq,lookahead);
    }

    public boolean isFinished()
    {
        return isFinished;
    }
}
