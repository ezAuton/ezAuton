package com.team2502.ezauton.actuators.implementations;

import com.team2502.ezauton.actuators.IVelocityMotor;
import com.team2502.ezauton.localization.sensors.ITranslationalDistanceSensor;
import com.team2502.ezauton.utils.Stopwatch;

/**
 * Describes a simulated motor with an encoder. The motor has infinite acceleration
 */
public class BaseSimulatedMotor implements IVelocityMotor, ITranslationalDistanceSensor
{
    private final Stopwatch stopwatch;
    protected double velocity = 0;
    private IVelocityMotor subscribed = null;
    private double position = 0;

    /**
     * Create a basic simulated motor
     *
     * @param stopwatch An instance of IStopwatch. Can either be simulated or real.
     */
    public BaseSimulatedMotor(Stopwatch stopwatch)
    {
        this.stopwatch = stopwatch;
    }

    /**
     * @return The motor to which the velocity is being applied
     */
    public IVelocityMotor getSubscribed()
    {
        return subscribed;
    }

    /**
     * Change the motor to which the velocity will be applied
     * @param subscribed The new motor instance
     */
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
