package com.github.ezauton.core.actuators.implementations;

import com.github.ezauton.core.localization.Updateable;
import com.github.ezauton.core.actuators.IVelocityMotor;
import com.github.ezauton.core.actuators.VelocityProcessor;
import com.github.ezauton.core.utils.IClock;
import com.github.ezauton.core.utils.Stopwatch;

import java.util.concurrent.TimeUnit;

/**
 * A velocity processor where the motor has a maximum accceleration
 */
public class RampUpVelocityProcessor extends VelocityProcessor implements Updateable
{

    private final double maxAccel;
    private final Stopwatch accelStopwatch;
    private double lastVelocity = 0;
    private double targetVelocity;

    /**
     * Create a RampUpVelocity processor
     *
     * @param velocityMotor The motor to apply the processed velocity to
     * @param clock         The clock to keep time with
     * @param maxAccel      The maximum acceleration of this motor.
     */
    public RampUpVelocityProcessor(IVelocityMotor velocityMotor, IClock clock, double maxAccel)
    {
        super(velocityMotor);
        accelStopwatch = new Stopwatch(clock).reset();
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
            lastVelocity = Math.min(lastVelocity + maxAccel * accelStopwatch.pop(TimeUnit.SECONDS), targetVelocity); // TODO: make this better and use triangle integral + stopwatch
        }
        else
        {
            lastVelocity = Math.max(lastVelocity - maxAccel * accelStopwatch.pop(TimeUnit.SECONDS), targetVelocity);
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
