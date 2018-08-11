package com.team2502.ezauton.actuators.implementations;

import com.team2502.ezauton.actuators.IVelocityMotor;
import com.team2502.ezauton.actuators.VelocityProcessor;
import com.team2502.ezauton.localization.Updateable;
import com.team2502.ezauton.utils.Stopwatch;

public class RampUpVelocityProcessor extends VelocityProcessor implements Updateable
{

    private final double maxAccel;
    private final Stopwatch accelStopwatch;
    private double lastVelocity = 0;
    private double targetVelocity;

    public RampUpVelocityProcessor(IVelocityMotor velocityMotor, Stopwatch stopwatch, double maxAccel)
    {
        super(velocityMotor);
        accelStopwatch = stopwatch;
        this.maxAccel = maxAccel;
    }

    public double getLastVelocity()
    {
        return lastVelocity;
    }

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

    @Override
    public void runVelocity(double targetVelocity)
    {
        accelStopwatch.reset();
        this.targetVelocity = targetVelocity;
    }
}
