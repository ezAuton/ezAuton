package com.team2502.ezauton.actuators;

import com.team2502.ezauton.utils.IStopwatch;

public class StaticFrictionSimulatedMotor extends RampUpSimulatedMotor
{

    private final double minVelMove;

    public StaticFrictionSimulatedMotor(IStopwatch stopwatch, double dv, double minVelMove)
    {
        super(stopwatch, dv);
        this.minVelMove = minVelMove;
    }

    @Override
    public void runVelocity(double targetVelocity)
    {
        if(getLastVelocity() != 0 || Math.abs(targetVelocity) >= minVelMove)
        {
            super.runVelocity(targetVelocity);
        }
    }
}
