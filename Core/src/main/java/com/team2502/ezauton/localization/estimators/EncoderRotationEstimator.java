package com.team2502.ezauton.localization.estimators;

import com.team2502.ezauton.localization.IRotationalLocationEstimator;
import com.team2502.ezauton.localization.ITranslationalLocationEstimator;
import com.team2502.ezauton.localization.Updateable;
import com.team2502.ezauton.localization.sensors.ITranslationalDistanceSensor;
import com.team2502.ezauton.trajectory.geometry.ImmutableVector;
import com.team2502.ezauton.utils.MathUtils;

//TODO: test
//TODO: Better name
/**
 * Describes an Updateable object that can track the location and heading of the robot
 */
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

    /**
     * Create an EncoderRotationEstimator
     * @param rotationalLocationEstimator An object that can estimate our current heading
     * @param distanceSensor              An encoder or encoder-like object.
     */
    public EncoderRotationEstimator(IRotationalLocationEstimator rotationalLocationEstimator, ITranslationalDistanceSensor distanceSensor)
    {
        this.rotationalLocationEstimator = rotationalLocationEstimator;
        this.distanceSensor = distanceSensor;
    }

    /**
     * Set the current position to <0, 0>, in effect resetting the location estimator
     */
    public void reset() //TODO: Reset heading
    {
        lastPosition = distanceSensor.getPosition();
        dPosVec = new ImmutableVector(0, 0);
        init = true;
    }


    @Override
    public double estimateHeading()
    {
        return rotationalLocationEstimator.estimateHeading();
    }

    /**
     * @return The current velocity vector of the robot in 2D space.
     */
    @Override
    public ImmutableVector estimateAbsoluteVelocity()
    {
        return MathUtils.Geometry.getVector(velocity, rotationalLocationEstimator.estimateHeading());
    }


    /**
     * @return The current location as estimated from the encoders
     */
    @Override
    public ImmutableVector estimateLocation()
    {
        return positionVec;
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
        if(rotationalLocationEstimator instanceof Updateable)
        {
            ((Updateable) rotationalLocationEstimator).update();
        }
        velocity = distanceSensor.getVelocity();
        dPos = distanceSensor.getPosition() - lastPosition;
        dPosVec = MathUtils.Geometry.getVector(dPos, rotationalLocationEstimator.estimateHeading());
        positionVec = positionVec.add(dPosVec);

        lastPosition = distanceSensor.getPosition();

        return true; //TODO: Return false sometimes?
    }
}
