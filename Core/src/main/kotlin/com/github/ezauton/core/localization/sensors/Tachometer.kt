package com.github.ezauton.core.localization.sensors

import com.github.ezauton.conversion.AngularVelocity

/**
 * A sensor which can measure revolutions / s (but not position)
 */
interface Tachometer : Sensor {
  /**
   * @return revolutions / s
   */
  val velocity: AngularVelocity
  companion object Mock {
    fun withVelocity(velocity: AngularVelocity): Tachometer {
      return object: Tachometer {
        override val velocity = velocity
      }
    }
  }
}
