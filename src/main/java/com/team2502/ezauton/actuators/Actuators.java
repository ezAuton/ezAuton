package com.team2502.ezauton.actuators;

import com.team2502.ezauton.utils.InterpolationMap;

public class Actuators
{
    public IVelocityMotor roughConvertVoltageToVel(IVoltageMotor voltageMotor, InterpolationMap velToVoltage)
    {
        return velocity -> voltageMotor.runVoltage(velToVoltage.get(voltageMotor));
    }

    public IVelocityMotor roughConvertVoltageToVel(IVoltageMotor voltageMotor, double maxSpeed)
    {
        InterpolationMap interpolationMap = new InterpolationMap(0D,0D);
        interpolationMap.put(maxSpeed,1D);
        return roughConvertVoltageToVel(voltageMotor, interpolationMap);
    }
}
