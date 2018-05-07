package com.team2502.ezauton.localization;

import com.team2502.ezauton.localization.sensors.ICompass;
import com.team2502.ezauton.utils.MathUtils;
import org.joml.ImmutableVector;

/**
 * Uses the navX to estimate angle of the robot using the gyro
 * (magnetometer = compass is too inaccurate and slow + it is bad w/ motors ... just overall horrible)
 * navX can also be used to estimate the absolute location of the robot using the accelerometer.
 * However, this is generally very inaccurate and should instead be done by combining this with
 * {@link EncoderAndCompassLocationEstimator}.
 */
public class CompassLocationEstimator implements IRotationalLocationEstimator
{
    private final ICompass compass;
    double initHeading = 0;

    /**
     * Make a new estimator for our angle
     */
    public CompassLocationEstimator(ICompass compass)
    {
        this.compass = compass;
    }

    /**
     * Read the value from the NavX and convert the angle to radians
     *
     * @return Theta of our robot in radians
     */
    @Override
    public double estimateHeading()
    {
        //TODO: Use ICompass.getRadians();
        // switch direction of increase
        double yawDegTotal = -compass.getDegrees();
        return MathUtils.Kinematics.navXToRad(yawDegTotal - initHeading);
    }
}
