package com.github.ezauton.core.localization.sensors

import com.github.ezauton.conversion.Angle
import com.github.ezauton.conversion.Distance
import com.github.ezauton.conversion.LinearVelocity
import com.github.ezauton.conversion.zero

/**
 * The combination of an encoder and a wheel. This allows to calculate translational distance. An encoder without
 * wheel specifications can only calculate revolutions.
 */
class EncoderWheel
/**
 * @param rotationalDistanceSensor The encoder for measuring revolutions
 * @param wheelDiameter The diameter of the wheel with the encoder (recommended in ft)
 */
  (private val rotationalDistanceSensor: RotationalDistanceSensor, private val wheelDiameter: Distance) :
  TranslationalDistanceSensor {
  /**
   * @param If there are additional gear ratios to consider, this is the multiplier
   * (wheel rev / encoder rev)
   */
  var multiplier = 1.0
  private var encoderPosMultiplied: Angle = zero()
  private var encoderRawPos: Angle = zero()

  /**
   * @return velocity (probably in ft/s)
   */
  override // because minute to second
  val velocity: LinearVelocity
    get() = rotationalDistanceSensor.velocity * Math.PI * wheelDiameter * multiplier

  /**
   * @return position (probably in ft)
   */
  override val position: Distance
    get() {
      val tempRawPos = rotationalDistanceSensor.position
      encoderPosMultiplied += (tempRawPos - encoderRawPos) * multiplier
      encoderRawPos = tempRawPos
      return encoderPosMultiplied * Math.PI * wheelDiameter
    }

  init {
    encoderPosMultiplied = rotationalDistanceSensor.position * multiplier
    encoderRawPos = rotationalDistanceSensor.position
  }
}
