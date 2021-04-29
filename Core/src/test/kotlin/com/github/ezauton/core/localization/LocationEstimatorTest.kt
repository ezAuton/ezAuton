package com.github.ezauton.core.localization

import com.github.ezauton.conversion.ConcreteVector
import com.github.ezauton.conversion.LinearVelocity
import com.github.ezauton.conversion.mps
import com.github.ezauton.conversion.vec
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LocationEstimatorTest {

  @Test
  fun `tank robot estimator interface`() {

    val estimator1 = object : TankRobotVelocityEstimator {
      override fun estimateAbsoluteVelocity(): ConcreteVector<LinearVelocity> {
        return vec(0.0, -3.0)
      }

      override val leftTranslationalWheelVelocity: LinearVelocity
        get() = (-3.0).mps

      override val rightTranslationalWheelVelocity: LinearVelocity
        get() = (-3.0).mps
    }

    assertEquals(3.0.mps, estimator1.avgTranslationalWheelSpeed)
    assertEquals((-3.0).mps, estimator1.avgTranslationalWheelVelocity)
  }
}
