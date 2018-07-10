package com.team2502.ezauton.actuators;

public abstract class VelocityProcessor implements IVelocityMotor
{

    private final IVelocityMotor toApply;

    public VelocityProcessor(IVelocityMotor toApply)
    {
        this.toApply = toApply;
    }

    public IVelocityMotor getToApply()
    {
        return toApply;
    }

}
