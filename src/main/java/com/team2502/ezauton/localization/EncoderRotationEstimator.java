package com.team2502.ezauton.localization;

import com.team2502.ezauton.localization.sensors.EncoderWheel;
import com.team2502.ezauton.robot.ITankRobot;
import com.team2502.ezauton.trajectory.geometry.ImmutableVector;
import com.team2502.ezauton.utils.MathUtils;

public class EncoderRotationEstimator implements IRotationalLocationEstimator, ITranslationalLocationEstimator, Updateable {

    private final ITankRobot tankRobot;
    private double lastPosLeft;
    private double lastPosRight;
    private boolean init = false;
    private final EncoderWheel left;
    private final EncoderWheel right;
    private double heading = 0;
    private ImmutableVector location = ImmutableVector.origin(2);

    public EncoderRotationEstimator(EncoderWheel left, EncoderWheel right, ITankRobot tankRobot)
    {
        this.left = left;
        this.right = right;
        this.tankRobot = tankRobot;
    }


    public void reset()
    {
        lastPosLeft = left.getPosition();
        lastPosRight = right.getPosition();
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

    @Override
    public boolean update() {
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
        heading+=MathUtils.Kinematics.getAngularDistance(dl,dr,tankRobot.getLateralWheelDistance());
        return true;
    }
}
