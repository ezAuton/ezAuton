package com.team2502.ezauton.actuators.implementations;

import com.team2502.ezauton.actuators.IVelocityMotor;
import com.team2502.ezauton.actuators.VelocityProcessor;

public class BoundedVelocityProcessor extends VelocityProcessor
{

    private final double maxVel;

    public BoundedVelocityProcessor(IVelocityMotor toApply, double maxVel)
    {
        super(toApply);
        this.maxVel = maxVel;
    }

    @Override
    public void runVelocity(double targetVelocity)
    {
        if(targetVelocity > maxVel)
        {
            getToApply().runVelocity(maxVel);
        }
        else if(targetVelocity < -maxVel)
        {
            getToApply().runVelocity(-maxVel);
        }
        else
        {
            getToApply().runVelocity(targetVelocity);
        }
    }
}
