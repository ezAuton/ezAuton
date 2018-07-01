package com.team2502.ezauton.actuators;

import com.team2502.ezauton.utils.IStopwatch;

public class RampUpSimulatedMotor extends InstantSimulatedMotor
{

    private final double dvMax;
    private double lastVelocity = 0;

    public RampUpSimulatedMotor(IStopwatch stopwatch, double dvMax)
    {
        super(stopwatch);
        this.dvMax = dvMax;
    }

    public double getLastVelocity()
    {
        return lastVelocity;
    }

    @Override
    public void runVelocity(double targetVelocity)
    {
        if(targetVelocity > velocity)
        {
            lastVelocity = Math.min(lastVelocity + dvMax, targetVelocity); // TODO: make this better and use triangle integral + stopwatch
        }
        else
        {
            lastVelocity = Math.max(lastVelocity - dvMax, targetVelocity);
        }
        super.runVelocity(lastVelocity);
    }
}
