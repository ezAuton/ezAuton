package com.team2502.ezauton.pathplanning.purepursuit;

import com.team2502.ezauton.localization.Updateable;
import com.team2502.ezauton.pathplanning.Path;
import com.team2502.ezauton.pathplanning.PathSegment;
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

    boolean brakeStage = false;
    boolean finishedPath = false;


    /**
     * Strategize your movement!
     *
     * @param waypoints                      A list of waypoints for the robot to drive through
     * @param lookahead
     */
    public PurePursuitMovementStrategy(List<ImmutableVector> waypoints)
    {
        ImmutableVector.assertSameDim(waypoints);
        this.path = Path.fromPoints(waypoints);
    }

    /**
     * @return The absolute location of the selected goal point.
     * The goal point is a point on the path 1 lookahead distance away from us.
     * We want to drive at it.
     * @see <a href="https://www.chiefdelphi.com/forums/showthread.php?threadid=162713">Velocity and End Behavior (Chief Delphi)</a>
     */
    private ImmutableVector calculateAbsoluteGoalPoint(double distanceCurrentSegmentLeft, double lookAheadDistance)
    {
        // The path is finished — there are no more goal points to compute
        if(brakeStage || finishedPath)
        {
            return null;
        }

        // The intersections with the path we are following and the circle around the robot of
        // radius lookAheadDistance. These intersections will determine the "goal point" we
        // will generate an arc to go to.

        return path.getGoalPoint(distanceCurrentSegmentLeft, lookAheadDistance);
    }

    /**
     * Recalculates position, heading, and goalpoint.
     */
    public ImmutableVector update(ImmutableVector pose, ImmutableVector closestPoint, double lookahead, double di)
    {

        if(pose == null)
        {
            throw new IllegalArgumentException("Pose cannot be null");
        }
        
        double absDistanceOfClosestPoint = path.getAbsDistanceOfClosestPoint(closestPoint);
        double distanceTo = path.getCurrent().getAbsoluteDistanceEnd() - absDistanceOfClosestPoint;

        double distanceLeftSq = path.getCurrent().getDistanceLeft(closestPoint);


        return calculateAbsoluteGoalPoint(distanceLeftSq,lookahead);
    }
}
