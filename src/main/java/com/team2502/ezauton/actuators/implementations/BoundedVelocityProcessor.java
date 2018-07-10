package com.team2502.ezauton.actuators.implementations;

import com.team2502.ezauton.actuators.IVelocityMotor;
import com.team2502.ezauton.actuators.VelocityProcessor;

public class BoundedVelocityProcessor extends VelocityProcessor
{

    private final double maxSpeed;

    public BoundedVelocityProcessor(IVelocityMotor toApply, double maxSpeed)
    {
        super(toApply);
        if(maxSpeed <= 0)
        {
            throw new IllegalArgumentException("maxSpeed must be a positive number!");
        }
        this.maxSpeed = maxSpeed;
    }

    @Override
    public void runVelocity(double targetVelocity)
    {
        if(targetVelocity > maxSpeed)
        {
            getToApply().runVelocity(maxSpeed);
        }
        else if(targetVelocity < -maxSpeed)
        {
            getToApply().runVelocity(-maxSpeed);
        }
        else
        {
            getToApply().runVelocity(targetVelocity);
        }
    }
}
