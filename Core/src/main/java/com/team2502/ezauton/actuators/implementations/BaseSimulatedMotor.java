package com.team2502.ezauton.actuators.implementations;

import com.team2502.ezauton.actuators.IVelocityMotor;
import com.team2502.ezauton.localization.sensors.ITranslationalDistanceSensor;
import com.team2502.ezauton.utils.IClock;
import com.team2502.ezauton.utils.Stopwatch;

import java.util.concurrent.TimeUnit;

/**
 * No ramp up.
 */
public class BaseSimulatedMotor implements IVelocityMotor, ITranslationalDistanceSensor
{
    private final Stopwatch stopwatch;

    /**
     * Assumed to be in dist/second
     */
    protected double velocity = 0;
    private IVelocityMotor subscribed = null;
    private double position = 0;

    public BaseSimulatedMotor(IClock clock)
    {
        this.stopwatch = new Stopwatch(clock);
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
        position += velocity * stopwatch.pop(TimeUnit.SECONDS) ; // Convert millis to seconds
        return position;
    }

    @Override
    public double getVelocity()
    {
        return velocity;
    }
}
