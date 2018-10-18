package org.github.ezauton.ezauton.localization;

import org.github.ezauton.ezauton.localization.sensors.ICompass;
import org.github.ezauton.ezauton.utils.MathUtils;

/**
 * Uses the navX to estimate angle of the robot using the gyro
 * (magnetometer = compass is too inaccurate and slow + it is bad w/ motors ... just overall horrible)
 * navX can also be used to estimate the absolute location of the robot using the accelerometer.
 * However, this is generally very inaccurate and should instead be done by combining this with
 * {@link TankRobotEncoderEncoderEstimator}
 */

/**
 * Describes an IRotationalLocationEstimator that uses a compass/compass-like sensor (like the gyro on the navX) to estimate the
 * robot's current heading
 */
public class CompassLocationEstimator implements IRotationalLocationEstimator
{
    private final ICompass compass;

    //TODO: Delete?
    double initHeading = 0;

    /**
     * Create a new CompassLocationEstimator
     *
     * @param compass A reference to the compass to use to estimate the robot's heading
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
