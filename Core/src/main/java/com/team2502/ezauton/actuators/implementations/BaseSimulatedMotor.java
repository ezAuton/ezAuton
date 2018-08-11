package com.team2502.ezauton.actuators.implementations;

import com.team2502.ezauton.actuators.IVelocityMotor;
import com.team2502.ezauton.localization.sensors.ITranslationalDistanceSensor;
import com.team2502.ezauton.utils.Stopwatch;

/**
 * No ramp up.
 */
public class BaseSimulatedMotor implements IVelocityMotor, ITranslationalDistanceSensor
{
    private final Stopwatch stopwatch;
    protected double velocity = 0;
    private IVelocityMotor subscribed = null;
    private double position = 0;

    public BaseSimulatedMotor(Stopwatch stopwatch)
    {
        this.stopwatch = stopwatch;
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
