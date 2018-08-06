package com.team2502.ezauton.actuators;

/**
 * Represents something that processes a target velocity before passing it on to the motor
 *
 * Useful for simulating static friction, non-infinite acceleration, etc
 */
public abstract class VelocityProcessor implements IVelocityMotor
{

    private final IVelocityMotor toApply;

    /**
     * Create a velocity processor.
     *
     * @param toApply To whom the processed velocity will be applied to
     */
    public VelocityProcessor(IVelocityMotor toApply)
    {
        this.toApply = toApply;
    }

    /**
     * @return The motor to whom the processed velocity gets applied to
     */
    public IVelocityMotor getToApply()
    {
        return toApply;
    } //TODO: Make protected

}
