package com.team2502.ezauton.actuators.implementations;

import com.team2502.ezauton.actuators.IVelocityMotor;
import com.team2502.ezauton.actuators.VelocityProcessor;
import com.team2502.ezauton.localization.sensors.ITranslationalDistanceSensor;

public class StaticFrictionVelocityProcessor extends VelocityProcessor
{

    private final double minVelMove;
    private final ITranslationalDistanceSensor distanceSensor;

    public StaticFrictionVelocityProcessor(ITranslationalDistanceSensor distanceSensor, IVelocityMotor toApply, double minVelMove)
    {
        super(toApply);
        this.minVelMove = minVelMove;
        this.distanceSensor = distanceSensor;
    }

    @Override
    public void runVelocity(double targetVelocity)
    {
        if(distanceSensor.getVelocity() != 0 || Math.abs(targetVelocity) >= minVelMove)
        {
            getToApply().runVelocity(targetVelocity);
        }
    }
}
