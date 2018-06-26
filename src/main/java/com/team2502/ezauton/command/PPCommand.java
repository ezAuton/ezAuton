package com.team2502.ezauton.command;

import com.team2502.ezauton.localization.ITranslationalLocationEstimator;
import com.team2502.ezauton.pathplanning.IPathSegment;
import com.team2502.ezauton.pathplanning.Path;
import com.team2502.ezauton.pathplanning.purepursuit.ILookahead;
import com.team2502.ezauton.pathplanning.purepursuit.PurePursuitMovementStrategy;
import com.team2502.ezauton.robot.subsystems.TranslationalLocationDriveable;
import com.team2502.ezauton.trajectory.geometry.ImmutableVector;

/**
 * A Pure Pursuit command which can be used in simulation or as a WPILib Command
 */
public class PPCommand implements ICommand
{
    private final PurePursuitMovementStrategy purePursuitMovementStrategy;
    private final ITranslationalLocationEstimator translationalLocationEstimator;
    private final ILookahead lookahead;
    private final TranslationalLocationDriveable translationalLocationDriveable;

    public PPCommand(PurePursuitMovementStrategy purePursuitMovementStrategy, ITranslationalLocationEstimator translationalLocationEstimator, ILookahead lookahead, TranslationalLocationDriveable translationalLocationDriveable)
    {
        this.purePursuitMovementStrategy = purePursuitMovementStrategy;
        this.translationalLocationEstimator = translationalLocationEstimator;
        this.lookahead = lookahead;
        this.translationalLocationDriveable = translationalLocationDriveable;
    }

    @Override
    public void execute()
    {
        ImmutableVector loc = translationalLocationEstimator.estimateLocation();
        ImmutableVector goalPoint = purePursuitMovementStrategy.update(loc, lookahead.getLookahead());
        if(purePursuitMovementStrategy.isFinished()) // to prevent null pointer
        {
            return;
        }
        Path path = purePursuitMovementStrategy.getPath();
        IPathSegment current = path.getCurrent();
        ImmutableVector closestPoint = current.getClosestPoint(loc);
        double absoluteDistance = current.getAbsoluteDistance(closestPoint);
        double speed = current.getSpeed(absoluteDistance);
        translationalLocationDriveable.driveTowardTransLoc(speed, goalPoint);
    }

    @Override
    public boolean isFinished()
    {
        return purePursuitMovementStrategy.isFinished();
    }
}
