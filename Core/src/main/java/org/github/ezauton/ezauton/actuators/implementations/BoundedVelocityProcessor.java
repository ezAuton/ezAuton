package org.github.ezauton.ezauton.actuators.implementations;

import org.github.ezauton.ezauton.actuators.IVelocityMotor;
import org.github.ezauton.ezauton.actuators.VelocityProcessor;

/**
 * A velocity processor that makes the target motor respect a maximum speed
 */
public class BoundedVelocityProcessor extends VelocityProcessor
{

    private final double maxSpeed;

    /**
     * Create a BoundedVelocityProcessor
     *
     * @param toApply  The motor to apply the processed velocity to
     * @param maxSpeed The maximum speed that the motor will be allowed to run at.
     */
    public BoundedVelocityProcessor(IVelocityMotor toApply, double maxSpeed)
    {
        super(toApply);
        if(maxSpeed <= 0)
        {
            throw new IllegalArgumentException("maxSpeed must be a positive number!");
        }
        this.maxSpeed = maxSpeed;
    }

    /**
     * Run the motor at a target velocity, unless the velocity is larger than this motor's maximum velocity
     *
     * @param targetVelocity The speed to run the motor at
     */
    @Override
    public void runVelocity(double targetVelocity)
    {
        if(targetVelocity > maxSpeed)
        {
            getToApply().runVelocity(maxSpeed);
        }
        else if(targetVelocity < -maxSpeed)
        {
            getToApply().runVelocity(-maxSpeed);
        }
        else
        {
            getToApply().runVelocity(targetVelocity);
        }
    }
}
