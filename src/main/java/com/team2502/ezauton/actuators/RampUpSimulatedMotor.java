package com.team2502.ezauton.actuators;

import com.team2502.ezauton.localization.Updateable;
import com.team2502.ezauton.utils.ICopyableStopwatch;
import com.team2502.ezauton.utils.RealStopwatch;

public class RampUpSimulatedMotor extends InstantSimulatedMotor implements Updateable
{

    private final double dvPerdt;
    private final ICopyableStopwatch accelStopwatch;
    private double lastVelocity = 0;
    private double targetVelocity;

    public RampUpSimulatedMotor(ICopyableStopwatch stopwatch, double dvPerdt)
    {
        super(stopwatch);
        accelStopwatch = stopwatch.copy();
        this.dvPerdt = dvPerdt;
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
        accelStopwatch.reset();
        this.targetVelocity = targetVelocity;
    }

    @Override
    public boolean update()
    {
        if(targetVelocity > lastVelocity)
        {
            lastVelocity = Math.min(lastVelocity + dvPerdt * accelStopwatch.pop(), targetVelocity); // TODO: make this better and use triangle integral + stopwatch
        }
        else
        {
            lastVelocity = Math.max(lastVelocity - dvPerdt * accelStopwatch.pop(), targetVelocity);
        }
        super.runVelocity(lastVelocity);
        return true;
    }
}
