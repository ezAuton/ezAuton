package com.team2502.ezauton.actuators;

import com.team2502.ezauton.utils.IStopwatch;

public class BoundedSFSimMotor extends StaticFrictionSimulatedMotor
{

    private final double maxVel;

    public BoundedSFSimMotor(IStopwatch stopwatch, double dv, double minVelMove, double maxVel)
    {
        super(stopwatch, dv, minVelMove);
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
    }
}
