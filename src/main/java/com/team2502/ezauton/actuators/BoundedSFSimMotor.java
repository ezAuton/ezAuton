package com.team2502.ezauton.actuators;

import com.team2502.ezauton.utils.ICopyableStopwatch;

public class BoundedSFSimMotor extends StaticFrictionSimulatedMotor
{

    private final double maxVel;

    public BoundedSFSimMotor(ICopyableStopwatch stopwatch, double dvOverdt, double minVelMove, double maxVel)
    {
        super(stopwatch, dvOverdt, minVelMove);
        this.maxVel = maxVel;
    }

    @Override
    public void runVelocity(double targetVelocity)
    {
        if(targetVelocity > maxVel)
        {
            super.runVelocity(maxVel);
        }
        else if(targetVelocity < -maxVel)
        {
            super.runVelocity(-maxVel);
        }
        else
        {
            super.runVelocity(targetVelocity);
        }
    }
}
