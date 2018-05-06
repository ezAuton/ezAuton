package com.team2502.ezauton.localization;

import com.team2502.ezauton.localization.sensors.IEncoder;
import com.team2502.ezauton.utils.IStopwatch;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.joml.ImmutableVector2f;

/**
 * Localization using encoders which can is primarily used for estimating the speed of the robot.
 * If an {@link IRotationalLocationEstimator} such as {@link CompassLocationEstimator} is added, it can do much more,
 * estimating the absolute position of the robot.
 */
public class EncoderAndCompassLocationEstimator implements ITranslationalLocationEstimator, ITranslationalVelocityEstimator
{
    private final IEncoder encoder;
    private final ImmutableVector2f location;
    private final IRotationalLocationEstimator rotEstimator;
    private final IStopwatch stopwatch;

    /**
     * Make a new position estimator
     *
     * @param rotEstimator a rotation estimator
     */
    public EncoderAndCompassLocationEstimator(IRotationalLocationEstimator rotEstimator, IEncoder encoder, IStopwatch stopwatch)
    {
        location = new ImmutableVector2f(0F, 0F);
        this.stopwatch = stopwatch;
        this.encoder = encoder;
        this.rotEstimator = rotEstimator;
    }

    /**
     * Estimate our location
     *
     * @return our location
     */
    @Override
    public ImmutableVector2f estimateLocation()
    {
        // figure out time since last estimated
        float dTime = stopwatch.pop();
        float leftVel = Robot.DRIVE_TRAIN.getLeftVel();
        float rightVel = Robot.DRIVE_TRAIN.getRightVel();

        // figure out how much our position has changed
        ImmutableVector2f dPos = MathUtils.Kinematics.getAbsoluteDPosLine(leftVel, rightVel, dTime, rotEstimator.estimateHeading());

        // add to our running total
        location = location.add(dPos);

        // logPop data on shuffleboard
        SmartDashboard.putNumber("posX", location.x);
        SmartDashboard.putNumber("posY", location.y);

        return location;
    }

    /**
     * @return A unit vector pointing in the direction of our movement.
     */
    @Override
    public ImmutableVector2f estimateAbsoluteVelocity()
    {
        return MathUtils.Geometry.getVector(estimateSpeed(), rotEstimator.estimateHeading());
    }

    @Override
    public float getLeftWheelSpeed()
    {
        return Robot.DRIVE_TRAIN.getLeftVel();
    }

    @Override
    public float getRightWheelSpeed()
    {
        return Robot.DRIVE_TRAIN.getRightVel();
    }

    @Override
    public float estimateSpeed()
    {
        return MathUtils.Kinematics.getTangentialSpeed(getLeftWheelSpeed(), getRightWheelSpeed());
    }
}

