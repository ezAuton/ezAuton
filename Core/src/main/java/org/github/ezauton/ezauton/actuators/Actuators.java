package org.github.ezauton.ezauton.actuators;

import org.github.ezauton.ezauton.utils.InterpolationMap;
import org.github.ezauton.ezauton.utils.OddInterpolationMap;

/**
 * Take in an input and have a mechanical input
 */
public class Actuators
{
    /**
     * Converts voltage drive to velocity drive. This is not 100% as it does not use encoders. The
     * interpolating map allows for mapping voltage to velocity if the relationship is non-linear
     * (note: most FRC motors are _very_ linear). Note: values will be different on different surfaces.
     *
     * @param voltageMotor
     * @param velToVoltage
     * @return
     */
    public static IVelocityMotor roughConvertVoltageToVel(IVoltageMotor voltageMotor, InterpolationMap velToVoltage)
    {
        return velocity -> voltageMotor.runVoltage(velToVoltage.get(velocity));
    }

    /**
     * Converts voltage drive to velocity drive. This is not 100% as it does not use encoders and assumes
     * the motor has a roughly linear relationship between voltage and velocity.
     *
     * @param voltageMotor
     * @param maxSpeed
     * @return
     */
    public static IVelocityMotor roughConvertVoltageToVel(IVoltageMotor voltageMotor, double maxSpeed)
    {
        InterpolationMap interpolationMap = new OddInterpolationMap(0D, 0D);
        interpolationMap.put(maxSpeed, 1D);
        return roughConvertVoltageToVel(voltageMotor, interpolationMap);
    }
}
