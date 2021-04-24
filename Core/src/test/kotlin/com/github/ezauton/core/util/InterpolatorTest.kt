package com.github.ezauton.core.util

import com.github.ezauton.core.utils.EvenInterpolationMap
import com.github.ezauton.core.utils.InterpolationMap
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class InterpolatorTest {

  private val interpMapRegular by lazy {

    val map = mapOf(
      1.0 to 2.0,
      2.0 to 1.0,
      4.0 to 3.0
    )
    InterpolationMap(map)
  }

  private val interpMapEven by lazy {
    val map = mapOf(
      0.0 to 1.0,
      1.0 to 2.0
    )

    EvenInterpolationMap(map)
  }

  @Test
  fun `test interpolator get`() {

    interpMapRegular.apply {
      assertEquals(2.0, this[0.0], 1E-6)
      assertEquals(2.0, this[1.0], 1E-6)
      assertEquals(1.5, this[1.5], 1E-6)
      assertEquals(1.0, this[2.0], 1E-6)
      assertEquals(2.0, this[3.0], 1E-6)
      assertEquals(3.0, this[4.0], 1E-6)
      assertEquals(3.0, this[5.0], 1E-6)
    }
  }

  @Test
  fun `test interpolator integrate`() {

    interpMapRegular.apply {
      assertEquals(2.0, integrate(0.0, 1.0), 1E-6)
      assertEquals(1.5, integrate(1.0, 2.0), 1E-6)
      assertEquals(2 * 2.0, integrate(2.0, 4.0), 1E-6)
      assertEquals(3.0, integrate(4.0, 5.0), 1E-6)
    }

    val integrate1 = interpMapRegular.integrate(0.0, 5.0)

    // [0,1] -> 2
    // [1,2] -> 1.5
    // [2,4] -> 2*2
    // [4,5] -> 3

    assertEquals(2 + (0.5 + 1) + 2 * 2 + 3, integrate1, 1E-6)
  }

  @Test
  fun `test even interpolator get`() {
    interpMapEven.also {
      it[0.0].testEquals(1.0)
      it[-1.0].testEquals(2.0)
      it[1.0].testEquals(2.0)
    }
  }

  @Test
  fun `test even interpolator integrate`() {

    interpMapEven.also {
      it.integrate(-1.0, 0.0).testEquals(3.0 / 2)
      it.integrate(0.0, 1.0).testEquals(3.0 / 2)

      it.integrate(-1.0, 1.0).testEquals(3.0)
    }
  }

  fun Double.testEquals(actual: Double, epsilon: Double = 1E-6) {
    assertEquals(actual, this, epsilon)
  }
}
