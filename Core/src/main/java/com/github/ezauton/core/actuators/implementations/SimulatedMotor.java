package com.github.ezauton.core.actuators.implementations;

import com.github.ezauton.core.actuators.VelocityMotor;
import com.github.ezauton.core.actuators.VoltageMotor;
import com.github.ezauton.core.localization.Updateable;
import com.github.ezauton.core.localization.UpdateableGroup;
import com.github.ezauton.core.localization.sensors.RotationalDistanceSensor;
import com.github.ezauton.core.utils.Clock;

/**
 * Unlike {@link BaseSimulatedMotor}, this motor has static friction and finite acceleration
 */
public class SimulatedMotor implements VelocityMotor, RotationalDistanceSensor, VoltageMotor, Updateable {

    private final BoundedVelocityProcessor motorConstraints;
    private final BaseSimulatedMotor motor;
    private final double kV;
    private final UpdateableGroup updateableGroup = new UpdateableGroup();
    private final double maxVoltage;

    /**
     * Create a simulated motor
     *
     * @param clock    The clock to keep track of time with
     * @param maxAccel The maximum acceleration of the motor in its gearbox.
     * @param minVel   The minimum velocity of the motor to achieve a non-zero speed outside of the gearbox.
     * @param maxVel   The maximum velocity of the motor
     * @param kV       Max voltage over max velocity (see FRC Drivetrain Characterization Paper eq. 11)). Used to simulate voltage-based driving as well.
     */
    public SimulatedMotor(Clock clock, double maxAccel, double minVel, double maxVel, double kV) {
        motor = new BaseSimulatedMotor(clock);
        this.kV = kV;

        RampUpVelocityProcessor leftRampUpMotor = new RampUpVelocityProcessor(motor, clock, maxAccel);
        updateableGroup.add(leftRampUpMotor);

        StaticFrictionVelocityProcessor leftSF = new StaticFrictionVelocityProcessor(motor, leftRampUpMotor, minVel);
        motorConstraints = new BoundedVelocityProcessor(leftSF, maxVel);
        maxVoltage = maxVel * kV;

    }

    @Override
    public void runVelocity(double targetVelocity) {
        motorConstraints.runVelocity(targetVelocity);
    }

    @Override
    public void runVoltage(double percentVoltage) {
        motorConstraints.runVelocity((maxVoltage * percentVoltage) / kV);
    }

    @Override
    public boolean update() {
        return updateableGroup.update();
    }

    @Override
    public double getPosition() {
        return motor.getPosition();
    }

    @Override
    public double getVelocity() {
        return motor.getVelocity();
    }
}
