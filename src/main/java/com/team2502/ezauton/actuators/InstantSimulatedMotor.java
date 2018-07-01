package com.team2502.ezauton.actuators;

import com.team2502.ezauton.localization.sensors.IEncoder;
import com.team2502.ezauton.utils.IStopwatch;

import java.util.HashSet;
import java.util.Set;

/**
 * No ramp up.
 */
public class InstantSimulatedMotor implements IVelocityMotor, IEncoder
{
    Set<IVelocityMotor> subscribers = new HashSet<>();

    protected double velocity = 0;

    private double position = 0;

    private final IStopwatch stopwatch;

    public InstantSimulatedMotor(IStopwatch stopwatch)
    {
        this.stopwatch = stopwatch;
    }

    @Override
    public void runVelocity(double targetVelocity)
    {
        stopwatch.resetIfNotInit();
        subscribers.forEach(motor -> motor.runVelocity(targetVelocity));
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
