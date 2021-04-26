package com.github.ezauton.core.actuators.implementations

import com.github.ezauton.core.actuators.VelocityMotor
import com.github.ezauton.core.actuators.VoltageMotor
import com.github.ezauton.core.localization.Updatable
import com.github.ezauton.core.localization.UpdatableGroup
import com.github.ezauton.core.localization.sensors.RotationalDistanceSensor
import com.github.ezauton.core.utils.Clock

/**
 * Unlike [BaseSimulatedMotor], this motor has static friction and finite acceleration
 */
class SimulatedMotor
/**
 * Create a simulated motor
 *
 * @param clock The clock to keep track of time with
 * @param maxAccel The maximum acceleration of the motor in its gearbox.
 * @param minVel The minimum velocity of the motor to achieve a non-zero speed outside of the gearbox.
 * @param maxVel The maximum velocity of the motor
 * @param kV Max voltage over max velocity (see FRC Drivetrain Characterization Paper eq. 11)). Used to simulate voltage-based driving as well.
 */
  (clock: Clock, maxAccel: Double, minVel: Double, maxVel: Double, private val kV: Double) : VelocityMotor, RotationalDistanceSensor, VoltageMotor, Updatable {

  private val motorConstraints: BoundedVelocityProcessor
  private val motor: BaseSimulatedMotor = BaseSimulatedMotor(clock)
  private val updatableGroup = UpdatableGroup()
  private val maxVoltage: Double

  override val position: Double
    get() = motor.position

  override val velocity: Double
    get() = motor.velocity

  init {

    val leftRampUpMotor = RampUpVelocityProcessor(motor, clock, maxAccel)
    updatableGroup.add(leftRampUpMotor)

    val leftSF = StaticFrictionVelocityProcessor(motor, leftRampUpMotor, minVel)
    motorConstraints = BoundedVelocityProcessor(leftSF, maxVel)
    maxVoltage = maxVel * kV
  }

  override fun runVelocity(targetVelocity: Double) {
    motorConstraints.runVelocity(targetVelocity)
  }

  override fun runVoltage(targetVoltage: Double) {
    motorConstraints.runVelocity(maxVoltage * targetVoltage / kV)
  }

  override fun update(): Boolean {
    return updatableGroup.update()
  }
}
