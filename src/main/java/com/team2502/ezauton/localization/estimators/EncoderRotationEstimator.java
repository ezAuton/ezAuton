package com.team2502.ezauton.localization.estimators;

import com.team2502.ezauton.localization.IRotationalLocationEstimator;
import com.team2502.ezauton.localization.ITranslationalLocationEstimator;
import com.team2502.ezauton.localization.Updateable;
import com.team2502.ezauton.localization.sensors.ITranslationalDistanceSensor;
import com.team2502.ezauton.trajectory.geometry.ImmutableVector;
import com.team2502.ezauton.utils.MathUtils;

//TODO: test
public class EncoderRotationEstimator implements IRotationalLocationEstimator, ITranslationalLocationEstimator, Updateable
{

    private final IRotationalLocationEstimator rotationalLocationEstimator;
    private final ITranslationalDistanceSensor distanceSensor;
    private double velocity;
    private double lastPosition;
    private double dPos;
    private ImmutableVector dPosVec;
    private ImmutableVector positionVec;
    private boolean init = false;

    public EncoderRotationEstimator(IRotationalLocationEstimator rotationalLocationEstimator, ITranslationalDistanceSensor distanceSensor)
    {
        this.rotationalLocationEstimator = rotationalLocationEstimator;
        this.distanceSensor = distanceSensor;
    }

    public void reset()
    {
        lastPosition = distanceSensor.getPosition();
        dPosVec = new ImmutableVector(0,0);
        init = true;
    }

    @Override
    public double estimateHeading()
    {
        return rotationalLocationEstimator.estimateHeading();
    }

    @Override
    public ImmutableVector estimateAbsoluteVelocity()
    {
        return MathUtils.Geometry.getVector(velocity,rotationalLocationEstimator.estimateHeading());
    }


    @Override
    public ImmutableVector estimateLocation()
    {
        return positionVec;
    }

    @Override
    public boolean update()
    {
        if(!init)
        {
            throw new IllegalArgumentException("Must be initialized! (call reset())");
        }
        if(rotationalLocationEstimator instanceof Updateable)
        {
            ((Updateable) rotationalLocationEstimator).update();
        }
        velocity = distanceSensor.getVelocity();
        dPos = distanceSensor.getPosition() - lastPosition;
        dPosVec = MathUtils.Geometry.getVector(dPos,rotationalLocationEstimator.estimateHeading());
        positionVec = positionVec.add(dPosVec);

        lastPosition = distanceSensor.getPosition();

        return true;
    }
}
