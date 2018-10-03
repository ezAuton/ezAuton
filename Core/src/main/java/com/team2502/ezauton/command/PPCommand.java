package com.team2502.ezauton.command;

import com.team2502.ezauton.localization.ITranslationalLocationEstimator;
import com.team2502.ezauton.pathplanning.IPathSegment;
import com.team2502.ezauton.pathplanning.Path;
import com.team2502.ezauton.pathplanning.purepursuit.ILookahead;
import com.team2502.ezauton.pathplanning.purepursuit.PurePursuitMovementStrategy;
import com.team2502.ezauton.robot.subsystems.TranslationalLocationDriveable;
import com.team2502.ezauton.trajectory.geometry.ImmutableVector;

import java.util.concurrent.TimeUnit;

/**
 * A Pure Pursuit command which can be used in simulation or as a WPILib Command
 */
public class PPCommand extends SimpleAction  // TODO: Rename to PPAction
{
    private final PurePursuitMovementStrategy purePursuitMovementStrategy;
    private final ITranslationalLocationEstimator translationalLocationEstimator;
    private final ILookahead lookahead;
    private final TranslationalLocationDriveable translationalLocationDriveable;
    private double speedUsed;
    private double absoluteDistanceUsed;

    /**
     * Create a PP Command
     *
     * @param timeUnit                       The timeunit that period is in
     * @param period                         How often to update estimated position, robot control, etc
     * @param purePursuitMovementStrategy    Our movement strategy.
     * @param translationalLocationEstimator An object that knows where we are on a 2D plane
     * @param lookahead                      An instance of {@link ILookahead} that can tell us how far along the path to look ahead
     * @param translationalLocationDriveable The drivetrain of the robot
     */
    public PPCommand(TimeUnit timeUnit, long period, PurePursuitMovementStrategy purePursuitMovementStrategy, ITranslationalLocationEstimator translationalLocationEstimator, ILookahead lookahead, TranslationalLocationDriveable translationalLocationDriveable)
    {
        super(timeUnit, period);
        this.purePursuitMovementStrategy = purePursuitMovementStrategy;
        this.translationalLocationEstimator = translationalLocationEstimator;
        this.lookahead = lookahead;
        this.translationalLocationDriveable = translationalLocationDriveable;
    }

    @Override
    public void execute()
    {
        // Find out where to drive to
        ImmutableVector loc = translationalLocationEstimator.estimateLocation();
        ImmutableVector goalPoint = purePursuitMovementStrategy.update(loc, lookahead.getLookahead());

        Path path = purePursuitMovementStrategy.getPath();
        IPathSegment current = path.getCurrent();
        ImmutableVector closestPoint = current.getClosestPoint(loc);
        absoluteDistanceUsed = current.getAbsoluteDistance(closestPoint);
        speedUsed = current.getSpeed(absoluteDistanceUsed);
        translationalLocationDriveable.driveTowardTransLoc(speedUsed, goalPoint);
    }

    public double getSpeedUsed()
    {
        return speedUsed;
    }

    public double getAbsoluteDistanceUsed()
    {
        return absoluteDistanceUsed;
    }

    @Override
    public boolean isFinished()
    {
        if(purePursuitMovementStrategy.isFinished() || getStopwatch().read(TimeUnit.SECONDS) > 5)
        {
            translationalLocationDriveable.driveSpeed(0);
            return true;
        }
        return false;
    }
}
