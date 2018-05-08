package com.team2502.ezauton.localization;

import com.team2502.ezauton.localization.sensors.IEncoder;
import com.team2502.ezauton.utils.MathUtils;
import com.team2502.ezauton.utils.IStopwatch;
import org.joml.ImmutableVector;

/**
 * Localization using encoders which can is primarily used for estimating the speed of the robot.
 * If an {@link IRotationalLocationEstimator} such as {@link CompassLocationEstimator} is added, it can do much more,
 * estimating the absolute position of the robot.
 */
public class EncoderAndCompassLocationEstimator implements ITranslationalLocationEstimator, ITranslationalVelocityEstimator
{
    private final IEncoder leftEncoder;
    private final IEncoder rightEncoder;
    private ImmutableVector location;
    private final IRotationalLocationEstimator rotEstimator;
    private final IStopwatch stopwatch;

    /**
     * Make a new position estimator
     *
     * @param rotEstimator a rotation estimator
     * @param rightEncoder
     */
    public EncoderAndCompassLocationEstimator(IRotationalLocationEstimator rotEstimator, IEncoder leftEncoder, IEncoder rightEncoder, IStopwatch stopwatch)
    {
        location = new ImmutableVector(0F, 0F);
        this.stopwatch = stopwatch;
        this.leftEncoder = leftEncoder;
        this.rightEncoder = rightEncoder;
        this.rotEstimator = rotEstimator;
    }

    /**
     * Estimate our location
     * <br>
     * To be accurate and up-to-date, this needs to run in a separate thread.
     *
     *
     * @return our location
     */
    @Override
    public ImmutableVector estimateLocation()
    {
        // figure out time since last estimated
        double dTime = stopwatch.pop();
        double leftVel = leftEncoder.getVelocity();
        double rightVel = rightEncoder.getVelocity();

        // figure out how much our position has changed
        ImmutableVector dPos = MathUtils.Kinematics.getAbsoluteDPosLine(leftVel, rightVel, dTime, rotEstimator.estimateHeading());

        // add to our running total
        location = location.add(dPos);

        return location;
    }

    /**
     * @return A unit vector pointing in the direction of our movement.
     */
    @Override
    public ImmutableVector estimateAbsoluteVelocity()
    {
        return MathUtils.Geometry.getVector(estimateSpeed(), rotEstimator.estimateHeading());
    }

    @Override
    public double getLeftWheelSpeed()
    {
        return leftEncoder.getVelocity();
    }

    @Override
    public double getRightWheelSpeed()
    {
        return rightEncoder.getVelocity();
    }

    @Override
    public double estimateSpeed()
    {
        return MathUtils.Kinematics.getTangentialSpeed(getLeftWheelSpeed(), getRightWheelSpeed());
    }
}

