package com.github.ezauton.core.actuators;

import com.github.ezauton.core.utils.InterpolationMap;
import com.github.ezauton.core.utils.OddInterpolationMap;

/**
 * Take in an input and have a mechanical input
 */
public class Actuators {
    /**
     * Converts voltage drive to velocity drive. This is not 100% as it does not use encoders. The
     * interpolating map allows for mapping voltage to velocity if the relationship is non-linear
     * (note: most FRC motors are _very_ linear). Note: values will be different on different surfaces.
     *
     * @param voltageMotor
     * @param velToVoltage
     * @return
     */
    public static VelocityMotor roughConvertVoltageToVel(VoltageMotor voltageMotor, InterpolationMap velToVoltage) {
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
    public static VelocityMotor roughConvertVoltageToVel(VoltageMotor voltageMotor, double maxSpeed) {
        InterpolationMap interpolationMap = new OddInterpolationMap(0D, 0D);
        interpolationMap.put(maxSpeed, 1D);
        return roughConvertVoltageToVel(voltageMotor, interpolationMap);
    }
}
