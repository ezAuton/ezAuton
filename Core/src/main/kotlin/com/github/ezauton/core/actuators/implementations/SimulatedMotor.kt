package com.github.ezauton.core.actuators.implementations

import com.github.ezauton.conversion.Angle
import com.github.ezauton.conversion.AngularAcceleration
import com.github.ezauton.conversion.AngularVelocity
import com.github.ezauton.core.actuators.RotVelMotor
import com.github.ezauton.core.actuators.VoltageMotor
import com.github.ezauton.core.localization.Updatable
import com.github.ezauton.core.localization.sensors.RotationalDistanceSensor

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
  (maxAccel: AngularAcceleration, minVel: AngularVelocity, maxVel: AngularVelocity, private val kV: Double) : RotVelMotor, RotationalDistanceSensor, VoltageMotor, Updatable {

  private val motorConstraints: BoundedVelocityProcessor
  private val motor: BaseSimulatedMotor = BaseSimulatedMotor()
  private val maxVelPerVolt: AngularVelocity

  private val toUpdate: Updatable

  init {

    val leftRampUpMotor = RampUpVelocityProcessor(motor, maxAccel)
    toUpdate = leftRampUpMotor
//    updatableGroup.add(leftRampUpMotor)

    val leftSF = StaticFrictionVelocityProcessor(motor, leftRampUpMotor, minVel)
    motorConstraints = BoundedVelocityProcessor(leftSF, maxVel)
    maxVelPerVolt = maxVel * kV // TODO: is this good
  }

  override fun runVelocity(targetVelocity: AngularVelocity) {
    motorConstraints.runVelocity(targetVelocity)
  }

  override fun runVoltage(targetVoltage: Double) {
    motorConstraints.runVelocity(maxVelPerVolt * targetVoltage / kV)
  }

  override val position: Angle get() = motor.position
  override val velocity: AngularVelocity get() = motor.velocity

  override fun update(): Boolean {
    return toUpdate.update()
  }
}
