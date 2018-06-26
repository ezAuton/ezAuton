package com.team2502.ezauton.command;

import com.team2502.ezauton.localization.ITranslationalLocationEstimator;
import com.team2502.ezauton.pathplanning.Path;
import com.team2502.ezauton.pathplanning.PathSegment;
import com.team2502.ezauton.pathplanning.purepursuit.ILookahead;
import com.team2502.ezauton.pathplanning.purepursuit.PurePursuitMovementStrategy;
import com.team2502.ezauton.robot.subsystems.TranslationalLocationDriveable;
import com.team2502.ezauton.trajectory.geometry.ImmutableVector;
import edu.wpi.first.wpilibj.command.Command;

public class PPCommand extends Command
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
    protected void execute()
    {
        ImmutableVector loc = translationalLocationEstimator.estimateLocation();
        ImmutableVector goalPoint = purePursuitMovementStrategy.update(loc, lookahead.getLookahead());
        Path path = purePursuitMovementStrategy.getPath();
        PathSegment current = path.getCurrent();
        ImmutableVector closestPoint = current.getClosestPoint(loc);
        double absoluteDistance = current.getAbsoluteDistance(closestPoint);
        translationalLocationDriveable.driveTowardTransLoc(current.getSpeed(absoluteDistance), goalPoint);
    }

    @Override
    protected boolean isFinished()
    {
        return purePursuitMovementStrategy.isFinished();
    }
}
