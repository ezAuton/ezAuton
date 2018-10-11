package org.github.ezauton.ezauton.action;

import org.github.ezauton.ezauton.localization.ITranslationalLocationEstimator;
import org.github.ezauton.ezauton.pathplanning.IPathSegment;
import org.github.ezauton.ezauton.pathplanning.Path;
import org.github.ezauton.ezauton.pathplanning.purepursuit.ILookahead;
import org.github.ezauton.ezauton.pathplanning.purepursuit.PurePursuitMovementStrategy;
import org.github.ezauton.ezauton.robot.subsystems.TranslationalLocationDriveable;
import org.github.ezauton.ezauton.trajectory.geometry.ImmutableVector;

import java.util.concurrent.TimeUnit;

/**
 * A Pure Pursuit action which can be used in simulation or as a WPILib Command
 */
public class PPCommand extends PeriodicAction  // TODO: Rename to PPAction
{
    private final PurePursuitMovementStrategy purePursuitMovementStrategy;
    private final ITranslationalLocationEstimator translationalLocationEstimator;
    private final ILookahead lookahead;
    private final TranslationalLocationDriveable translationalLocationDriveable;

    /**
     * Create a PP Command
     *  @param period                         How often to update estimated position, robot control, etc
     * @param timeUnit                       The timeunit that period is in
     * @param purePursuitMovementStrategy    Our movement strategy.
     * @param translationalLocationEstimator An object that knows where we are on a 2D plane
     * @param lookahead                      An instance of {@link ILookahead} that can tell us how far along the path to look ahead
     * @param translationalLocationDriveable The drivetrain of the robot
     */
    public PPCommand(long period, TimeUnit timeUnit, PurePursuitMovementStrategy purePursuitMovementStrategy, ITranslationalLocationEstimator translationalLocationEstimator, ILookahead lookahead, TranslationalLocationDriveable translationalLocationDriveable)
    {
        super(period, timeUnit);
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
        double absoluteDistance = current.getAbsoluteDistance(closestPoint);
        double speed = current.getSpeed(absoluteDistance);
        translationalLocationDriveable.driveTowardTransLoc(speed, goalPoint);
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
