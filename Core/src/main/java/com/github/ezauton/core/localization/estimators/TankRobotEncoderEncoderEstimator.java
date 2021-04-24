package com.github.ezauton.core.localization.estimators;

import com.github.ezauton.core.localization.RotationalLocationEstimator;
import com.github.ezauton.core.localization.TankRobotVelocityEstimator;
import com.github.ezauton.core.localization.TranslationalLocationEstimator;
import com.github.ezauton.core.localization.Updateable;
import com.github.ezauton.core.localization.sensors.TranslationalDistanceSensor;
import com.github.ezauton.core.robot.TankRobotConstants;
import com.github.ezauton.core.trajectory.geometry.ImmutableVector;
import com.github.ezauton.core.utils.MathUtils;

/**
 * Describes an object that can estimate the heading and absolute position of the robot solely using the encoders
 */
public final class TankRobotEncoderEncoderEstimator implements RotationalLocationEstimator, TranslationalLocationEstimator, TankRobotVelocityEstimator, Updateable {

    private final TankRobotConstants tankRobot;
    private final TranslationalDistanceSensor left;
    private final TranslationalDistanceSensor right;
    private double lastPosLeft;
    private double lastPosRight;
    private boolean init = false;
    private double heading = 0;
    private ImmutableVector location = ImmutableVector.origin(2);

    /**
     * Create a TankRobotEncoderEstimator
     *
     * @param left      A reference to the encoder on the left side of the robot
     * @param right     A reference to the encoder on the right side of the robot
     * @param tankRobot A reference to an object containing data about the structure of the drivetrain
     */
    public TankRobotEncoderEncoderEstimator(TranslationalDistanceSensor left, TranslationalDistanceSensor right, TankRobotConstants tankRobot) {
        this.left = left;
        this.right = right;
        this.tankRobot = tankRobot;
    }

    /**
     * Reset the heading and position of the location estimator
     */
    public void reset() //TODO: Suggestion -- Have an IPoseEstimator that implements Updateable, IRotationalEstimator, TranslationalLocationEstimator that also has a reset method
    {
        lastPosLeft = left.getPosition();
        lastPosRight = right.getPosition();
        location = new ImmutableVector(0, 0);
        heading = 0D;
        init = true;
    }

    @Override
    public double estimateHeading() {
        return heading;
    }

    @Override
    public ImmutableVector estimateLocation() {
        return location;
    }

    /**
     * Update the calculation for the current heading and position. Call this as frequently as possible to ensure optimal results
     *
     * @return True
     */
    @Override
    public boolean update() {
        if (!init) {
            throw new IllegalArgumentException("Must be initialized! (call reset())");
        }

        double leftPosition = left.getPosition();
        double dl = leftPosition - lastPosLeft;
        double rightPosition = right.getPosition();
        double dr = rightPosition - lastPosRight;

        lastPosLeft = leftPosition;
        lastPosRight = rightPosition;

        ImmutableVector dLocation = MathUtils.Kinematics.getAbsoluteDPosCurve(dl, dr, tankRobot.getLateralWheelDistance(), heading);
        if (!dLocation.isFinite()) {
            throw new IllegalStateException("dLocation is " + dLocation + ", which is not finite! dl = " + dl + ", dr = " + dr + ", heading = " + heading);
        }
        location = location.add(dLocation);
        heading += MathUtils.Kinematics.getAngularDistance(dl, dr, tankRobot.getLateralWheelDistance());
        return true;
    }

    /**
     * @return The current velocity vector of the robot in 2D space.
     */
    @Override
    public ImmutableVector estimateAbsoluteVelocity() {
        return MathUtils.Geometry.getVector(getAvgTranslationalWheelVelocity(), heading);
    }

    @Override
    public double getLeftTranslationalWheelVelocity() {
        return left.getVelocity();
    }

    @Override
    public double getRightTranslationalWheelVelocity() {
        return right.getVelocity();
    }
}
