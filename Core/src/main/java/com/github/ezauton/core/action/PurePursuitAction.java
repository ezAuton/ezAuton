package com.github.ezauton.core.action;

import com.github.ezauton.core.localization.TranslationalLocationEstimator;
import com.github.ezauton.core.pathplanning.PathSegment;
import com.github.ezauton.core.pathplanning.Path;
import com.github.ezauton.core.pathplanning.purepursuit.Lookahead;
import com.github.ezauton.core.pathplanning.purepursuit.PurePursuitMovementStrategy;
import com.github.ezauton.core.robot.subsystems.TranslationalLocationDriveable;
import com.github.ezauton.core.trajectory.geometry.ImmutableVector;

import java.util.concurrent.TimeUnit;

/**
 * A Pure Pursuit action which can be used in simulation or as a WPILib Command
 */
public final class PurePursuitAction extends PeriodicAction  // TODO: Rename to PPAction
{
    private final PurePursuitMovementStrategy purePursuitMovementStrategy;
    private final TranslationalLocationEstimator translationalLocationEstimator;
    private final Lookahead lookahead;
    private final TranslationalLocationDriveable translationalLocationDriveable;
    private double speedUsed;
    private double absoluteDistanceUsed;

    /**
     * Create a PP Command
     *
     * @param period                         How often to update estimated position, robot control, etc
     * @param timeUnit                       The timeunit that period is in
     * @param purePursuitMovementStrategy    Our movement strategy.
     * @param translationalLocationEstimator An object that knows where we are on a 2D plane
     * @param lookahead                      An instance of {@link Lookahead} that can tell us how far along the path to look ahead
     * @param translationalLocationDriveable The drivetrain of the robot
     */
    public PurePursuitAction(long period, TimeUnit timeUnit, PurePursuitMovementStrategy purePursuitMovementStrategy, TranslationalLocationEstimator translationalLocationEstimator, Lookahead lookahead, TranslationalLocationDriveable translationalLocationDriveable) {
        super(period, timeUnit);
        this.purePursuitMovementStrategy = purePursuitMovementStrategy;
        this.translationalLocationEstimator = translationalLocationEstimator;
        this.lookahead = lookahead;
        this.translationalLocationDriveable = translationalLocationDriveable;
    }

    @Override
    public void execute() {
        // Find out where to drive to
        ImmutableVector loc = translationalLocationEstimator.estimateLocation();
        ImmutableVector goalPoint = purePursuitMovementStrategy.update(loc, lookahead.getLookahead());

        Path path = purePursuitMovementStrategy.getPath();
        PathSegment current = path.getCurrent();
        ImmutableVector closestPoint = current.getClosestPoint(loc);
        absoluteDistanceUsed = current.getAbsoluteDistance(closestPoint);
        speedUsed = current.getSpeed(absoluteDistanceUsed);
        translationalLocationDriveable.driveTowardTransLoc(speedUsed, goalPoint);
    }

    /**
     * @return The most reset speed used by Pure Pursuit
     */
    public double getSpeedUsed() {
        return speedUsed;
    }

    /**
     * @return The most reset absolute distance used by Pure Pursuit
     */
    public double getAbsoluteDistanceUsed() {
        return absoluteDistanceUsed;
    }

    @Override
    public boolean isFinished() {
        if (purePursuitMovementStrategy.isFinished()) {
            translationalLocationDriveable.driveSpeed(0);
            return true;
        }
        return false;
    }
}
