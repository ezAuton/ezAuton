package com.team2502.ezauton.actuators.implementations;

import com.team2502.ezauton.actuators.IVelocityMotor;
import com.team2502.ezauton.actuators.VelocityProcessor;
import com.team2502.ezauton.localization.Updateable;
import com.team2502.ezauton.utils.IStopwatch;

/**
 * A velocity processor where the motor has a maximum accceleration
 */
public class RampUpVelocityProcessor extends VelocityProcessor implements Updateable
{

    private final double maxAccel;
    private final IStopwatch accelStopwatch;
    private double lastVelocity = 0;
    private double targetVelocity;

    /**
     * Create a RampUpVelocity processor
     *
     * @param velocityMotor The motor to apply the processed velocity to
     * @param stopwatch     A stopwatch which will be used to calculate velocity from acceleration. Can be simulated or real.
     * @param maxAccel      The maximum acceleration of this motor.
     */
    public RampUpVelocityProcessor(IVelocityMotor velocityMotor, IStopwatch stopwatch, double maxAccel)
    {
        super(velocityMotor);
        accelStopwatch = stopwatch;
        this.maxAccel = maxAccel;
    }

    public double getLastVelocity()
    {
        return lastVelocity;
    }

    /**
     * Update the motor to simulate acceleration over time
     *
     * @return True
     */
    @Override
    public boolean update()
    {
        if(targetVelocity > lastVelocity)
        {
            lastVelocity = Math.min(lastVelocity + maxAccel * accelStopwatch.pop(), targetVelocity); // TODO: make this better and use triangle integral + stopwatch
        }
        else
        {
            lastVelocity = Math.max(lastVelocity - maxAccel * accelStopwatch.pop(), targetVelocity);
        }
        getToApply().runVelocity(lastVelocity);
        return true;
    }

    /**
     * Make the motor accelerate up to a new velocity
     *
     * @param targetVelocity The new target velocity
     */
    @Override
    public void runVelocity(double targetVelocity)
    {
        accelStopwatch.reset();
        this.targetVelocity = targetVelocity;
    }
}
