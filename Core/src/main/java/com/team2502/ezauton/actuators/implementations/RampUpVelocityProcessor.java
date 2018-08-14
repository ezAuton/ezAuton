package com.team2502.ezauton.actuators.implementations;

import com.team2502.ezauton.actuators.IVelocityMotor;
import com.team2502.ezauton.actuators.VelocityProcessor;
import com.team2502.ezauton.localization.Updateable;
import com.team2502.ezauton.utils.IClock;
import com.team2502.ezauton.utils.Stopwatch;
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
        accelStopwatch = new Stopwatch(clock);
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
