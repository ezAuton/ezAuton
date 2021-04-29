package com.github.ezauton.core.math

import com.github.ezauton.conversion.svec
import com.github.ezauton.core.utils.math.cross
import com.github.ezauton.core.utils.math.perp
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class VectorTest {

  @Test
  fun testVectorConstructors() {
    assertNotEquals(svec(1), svec(1, 1))
  }

  @Test
  fun testAccessingComponents() {
    val a = svec(123, 155)

    assertEquals(123.0, a[0], DELTA)
    assertEquals(123.0, a[0], DELTA)
    assertEquals(155.0, a[1], DELTA)
    assertEquals(155.0, a[1], DELTA)
  }

  @Test
  fun testOnly2Components() {
    val a = svec(123, 155)
    assertThrows(IndexOutOfBoundsException::class.java) { a.get(2) }
  }

  @Test
  fun testPerpendicular() {
    assertEquals(j * (-1.0), perp(i))
    assertNotEquals(i, perp(i))
    assertEquals(origin, perp(origin))

    assertEquals(svec(0, 0, 1), cross(svec(1, 0, 0), svec(0, 1, 0)))
  }

  companion object {
    private const val DELTA = 1E-5
    private val i = svec(1, 0)
    private val j = svec(0, 1)
    private val origin = svec(0, 0)
  }
}
