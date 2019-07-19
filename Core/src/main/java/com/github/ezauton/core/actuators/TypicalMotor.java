package com.github.ezauton.core.actuators;


import com.github.ezauton.core.localization.sensors.RotationalDistanceSensor;

/**
 * An interface representing your typical motor. It is able to be controller by either velocity or voltage, and has an encoder.\
 */
public interface TypicalMotor extends VelocityMotor, RotationalDistanceSensor, VoltageMotor {
}
