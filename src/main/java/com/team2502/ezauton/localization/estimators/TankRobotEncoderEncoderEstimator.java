package com.team2502.ezauton.localization.estimators;

import com.team2502.ezauton.localization.IRotationalLocationEstimator;
import com.team2502.ezauton.localization.ITankRobotVelocityEstimator;
import com.team2502.ezauton.localization.ITranslationalLocationEstimator;
import com.team2502.ezauton.localization.Updateable;
import com.team2502.ezauton.localization.sensors.ITranslationalDistanceSensor;
import com.team2502.ezauton.robot.ITankRobotConstants;
import com.team2502.ezauton.trajectory.geometry.ImmutableVector;
import com.team2502.ezauton.utils.MathUtils;

//TODO: Better name

/**
 * Describes an object that can estimate the heading and absolute position of the robot solely using the encoders
 */
public class TankRobotEncoderEncoderEstimator implements IRotationalLocationEstimator, ITranslationalLocationEstimator, ITankRobotVelocityEstimator, Updateable
{

    private final ITankRobotConstants tankRobot;
    private final ITranslationalDistanceSensor left;
    private final ITranslationalDistanceSensor right;
    private double lastPosLeft;
    private double lastPosRight;
    private boolean init = false;
    private double heading = 0;
    private ImmutableVector location = ImmutableVector.origin(2);

    /**
     * Create a TankRobotEncoderEstimator
     * @param left A reference to the encoder on the left side of the robot
     * @param right A reference to the encoder on the right side of the robot
     * @param tankRobot A reference to an object containing data about the structure of the drivetrain
     */
    public TankRobotEncoderEncoderEstimator(ITranslationalDistanceSensor left, ITranslationalDistanceSensor right, ITankRobotConstants tankRobot)
    {
        this.left = left;
        this.right = right;
        this.tankRobot = tankRobot;
    }

    /**
     * Reset the heading and position of the location estimator
     */
    public void reset() //TODO: Suggestion -- Have an IPoseEstimator that implements Updateable, IRotationalEstimator, ITranslationalLocationEstimator that also has a reset method
    {
        lastPosLeft = left.getPosition();
        lastPosRight = right.getPosition();
        location = new ImmutableVector(0, 0);
        heading = 0D;
        init = true;
    }

    @Override
    public double estimateHeading()
    {
        return heading;
    }

    @Override
    public ImmutableVector estimateLocation()
    {
        return location;
    }

    /**
     * Update the calculation for the current heading and position. Call this as frequently as possible to ensure optimal results
     * @return True
     */
    @Override
    public boolean update()
    {
        if(!init)
        {
            throw new IllegalArgumentException("Must be initialized! (call reset())");
        }

        double leftPosition = left.getPosition();
        double dl = leftPosition - lastPosLeft;
        double rightPosition = right.getPosition();
        double dr = rightPosition - lastPosRight;

        lastPosLeft = leftPosition;
        lastPosRight = rightPosition;

        ImmutableVector dLocation = MathUtils.Kinematics.getAbsoluteDPosCurve(dl, dr, tankRobot.getLateralWheelDistance(), heading);
        location = location.add(dLocation);
        heading += MathUtils.Kinematics.getAngularDistance(dl, dr, tankRobot.getLateralWheelDistance());
        return true;
    }

    /**
     * @return The current velocity vector of the robot in 2D space.
     */
    @Override
    public ImmutableVector estimateAbsoluteVelocity()
    {
        return MathUtils.Geometry.getVector(getAvgTranslationalWheelVelocity(), heading);
    }

    @Override
    public double getLeftTranslationalWheelVelocity()
    {
        return left.getVelocity();
    }

    @Override
    public double getRightTranslationalWheelVelocity()
    {
        return right.getVelocity();
    }
}
