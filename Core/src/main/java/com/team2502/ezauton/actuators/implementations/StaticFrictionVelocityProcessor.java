package com.team2502.ezauton.actuators.implementations;

import com.team2502.ezauton.actuators.IVelocityMotor;
import com.team2502.ezauton.actuators.VelocityProcessor;
import com.team2502.ezauton.localization.sensors.ITranslationalDistanceSensor;

/**
 * A velocity processor that does not run the motor if the target velocity is less than the minimum velocity that is required to move.
 */
public class StaticFrictionVelocityProcessor extends VelocityProcessor
{

    private final double minVelMove;
    private final ITranslationalDistanceSensor distanceSensor;

    /**
     * Create a StaticFrictionVelocityProcessor
     *
     * @param distanceSensor An encoder
     * @param toApply        The motor to apply the processed velocity to
     * @param minVelMove     The minimum velocity to move the motor
     */
    public StaticFrictionVelocityProcessor(ITranslationalDistanceSensor distanceSensor, IVelocityMotor toApply, double minVelMove)
    {
        super(toApply);
        this.minVelMove = minVelMove;
        this.distanceSensor = distanceSensor;
    }

    /**
     * Run the motor at the target velocity, unless the target velocity is too small and we are not moving
     *
     * @param targetVelocity The speed to run the motor at
     */
    @Override
    public void runVelocity(double targetVelocity)
    {
        if(distanceSensor.getVelocity() != 0 || Math.abs(targetVelocity) >= minVelMove)
        {
            getToApply().runVelocity(targetVelocity);
        }
    }
}
