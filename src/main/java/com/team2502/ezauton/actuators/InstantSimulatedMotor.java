package com.team2502.ezauton.actuators;

import com.team2502.ezauton.localization.sensors.ITranslationalDistanceSensor;
import com.team2502.ezauton.utils.IStopwatch;
import com.team2502.ezauton.utils.RealStopwatch;

/**
 * No ramp up.
 */
public class InstantSimulatedMotor implements IVelocityMotor, ITranslationalDistanceSensor
{
    private final IStopwatch stopwatch;
    protected double velocity = 0;
    private IVelocityMotor subscribed = null;
    private double position = 0;

    public InstantSimulatedMotor(IStopwatch stopwatch)
    {
        this.stopwatch = stopwatch;
    }

    public static InstantSimulatedMotor fromVolt(IVoltageMotor voltageMotor, double maxSpeed)
    {
        InstantSimulatedMotor motor = new InstantSimulatedMotor(new RealStopwatch());
        motor.subscribed = Actuators.roughConvertVoltageToVel(voltageMotor, maxSpeed);
        return motor;
    }

    public IVelocityMotor getSubscribed()
    {
        return subscribed;
    }

    public void setSubscribed(IVelocityMotor subscribed)
    {
        this.subscribed = subscribed;
    }

    @Override
    public void runVelocity(double targetVelocity)
    {
        stopwatch.resetIfNotInit();
        if(subscribed != null)
        {
            subscribed.runVelocity(targetVelocity);
        }
        position += velocity * stopwatch.pop();
        this.velocity = targetVelocity;
    }

    @Override
    public double getPosition()
    {
        position += velocity * stopwatch.pop();
        return position;
    }

    @Override
    public double getVelocity()
    {
        return velocity;
    }
}
