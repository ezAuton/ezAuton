package com.github.ezauton.core.actuators

//import com.github.ezauton.core.utils.LinearInterpolationMap
//import com.github.ezauton.core.utils.OddInterpolationMap
//
///**
// * Take in an input and have a mechanical input
// */
//object Actuators {
//  /**
//   * Converts voltage drive to velocity drive. This is not 100% as it does not use encoders. The
//   * interpolating map allows for mapping voltage to velocity if the relationship is non-linear
//   * (note: most FRC motors are _very_ linear). Note: values will be different on different surfaces.
//   *
//   * @param voltageMotor
//   * @param velToVoltage
//   * @return
//   */
//  fun roughConvertVoltageToVel(voltageMotor: VoltageMotor, velToVoltage: LinearInterpolationMap) =
//    { velocity: Double ->
//      voltageMotor.runVoltage(velToVoltage[velocity])
//    }
//}
//
///**
// * Converts voltage drive to velocity drive. This is not 100% as it does not use encoders and assumes
// * the motor has a roughly linear relationship between voltage and velocity.
// *
// * @param voltageMotor
// * @param maxSpeed
// * @return
// */
//fun roughConvertVoltageToVel(voltageMotor: VoltageMotor, maxSpeed: Double): VelocityMotor {
//  val interpolationMap = OddInterpolationMap(0.0, 0.0)
//  interpolationMap[maxSpeed] = 1.0
//  return roughConvertVoltageToVel(voltageMotor, interpolationMap)
//}
//}
