package com.github.ezauton.core.math

import com.github.ezauton.conversion.ScalarVector
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class VectorTest {

  @Test
  fun testVectorConstructors() {
    assertNotEquals(ScalarVector(1), ScalarVector(1, 1))
  }

  @Test
  fun testAccessingComponents() {
    val a = ScalarVector(123, 155)

    assertEquals(123.0, a.get(0), DELTA)
    assertEquals(123.0, a.get(0), DELTA)
    assertEquals(155.0, a.get(1), DELTA)
    assertEquals(155.0, a.get(1), DELTA)
  }

  @Test
  fun testOnly2Components() {
    val a = ScalarVector(123, 155)
    assertThrows(IndexOutOfBoundsException::class.java) { a.get(2) }
  }

  @Test
  fun testPerpendicular() {
    assertEquals(j.mul(-1.0), MathUtils.perp(i))
    assertNotEquals(i, MathUtils.perp(i))
    assertEquals(origin, MathUtils.perp(origin))

    assertEquals(ScalarVector(0, 0, 1), MathUtils.cross(ScalarVector(1, 0, 0), ScalarVector(0, 1, 0)))
  }

  companion object {
    private val DELTA = 1E-5
    private val i = ScalarVector(1, 0)
    private val j = ScalarVector(0, 1)
    private val origin = ScalarVector(0, 0)
  }
}
