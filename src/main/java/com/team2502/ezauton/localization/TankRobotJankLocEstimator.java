package com.team2502.ezauton.localization;

import com.team2502.ezauton.localization.sensors.EncoderWheel;
import com.team2502.ezauton.localization.sensors.ICompass;
import com.team2502.ezauton.trajectory.geometry.ImmutableVector;

public class TankRobotJankLocEstimator implements IRotationalLocationEstimator, ITranslationalLocationEstimator, ITankRobotVelocityEstimator, Updateable
{

    private final ICompass compass;
    private final EncoderWheel left;
    private final EncoderWheel right;

    public TankRobotJankLocEstimator(ICompass compass, EncoderWheel left, EncoderWheel right)
    {
        this.compass = compass;
        this.left = left;
        this.right = right;
    }

    @Override
    public double estimateHeading()
    {
        return 0;
    }

    @Override
    public ImmutableVector estimateAbsoluteVelocity()
    {
        return null;
    }

    @Override
    public double getLeftTranslationalWheelVelocity()
    {
        return 0;
    }

    @Override
    public double getRightTranslationalWheelVelocity()
    {
        return 0;
    }

    @Override
    public ImmutableVector estimateLocation()
    {
        return null;
    }

    @Override
    public boolean update()
    {
        return false;
    }
}
