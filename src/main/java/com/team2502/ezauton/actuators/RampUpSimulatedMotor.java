package com.team2502.ezauton.actuators;

import com.team2502.ezauton.utils.IStopwatch;
import com.team2502.ezauton.utils.RealStopwatch;

public class RampUpSimulatedMotor extends InstantSimulatedMotor
{

    private final double dvMax;
    private double lastVelocity = 0;

    public RampUpSimulatedMotor(IStopwatch stopwatch, double dvMax)
    {
        super(stopwatch);
        this.dvMax = dvMax;
    }

    public static RampUpSimulatedMotor fromVolt(IVoltageMotor voltageMotor, double maxSpeed, double dvMax)
    {
        RampUpSimulatedMotor motor = new RampUpSimulatedMotor(new RealStopwatch(), dvMax);
        motor.setSubscribed(Actuators.roughConvertVoltageToVel(voltageMotor, maxSpeed));
        return motor;
    }

    public double getLastVelocity()
    {
        return lastVelocity;
    }

    @Override
    public void runVelocity(double targetVelocity)
    {
        if(targetVelocity > lastVelocity)
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
