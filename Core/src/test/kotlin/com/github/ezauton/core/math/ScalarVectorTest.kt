package com.github.ezauton.core.math

import com.github.ezauton.conversion.ScalarVector
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.math.sqrt

class ScalarVectorTest {

  @Test
  fun testWrongSize() {
    assertThrows<IllegalArgumentException>(IllegalArgumentException::class.java) { ScalarVector(1.0, 1.0).assertDimension(1) }
  }

  fun testCollectionWrongSize() {
    val oddOneOut = ScalarVector(listOf(1.0, 2.0, 3.0, 4.0, 5.0))

    val vectors = ArrayList<ScalarVector>()
    for (i in 0..4) {
      vectors.add(ScalarVector(1.0, 2.0, 3.0, 4.0))
    }
    vectors.add(oddOneOut)
    // aaaaaaaaa

    assertThrows(IllegalArgumentException::class.java) { ScalarVector.assertSameDim(vectors) }
  }

  @Test
  fun testEq() {
    assertEquals(ScalarVector(1.0, 1.0), ScalarVector(1.0, 1.0))
    assertNotEquals(ScalarVector(1.0, 1.0), ScalarVector(2.0, 1.0))
  }

  @Test
  fun testOf() {
    assertEquals(ScalarVector(4.0, 4.0, 4.0), ScalarVector.of(4.0, 3))
  }

  @Test
  fun testMultiply() {
    assertEquals(ScalarVector(3.0, 6.0, 9.0), ScalarVector(1.0, 2.0, 3.0) * 3.0)
  }

  @Test
  fun testDot() {
    assertEquals(0.0, ScalarVector(1.0, 0.0, 0.0).dot(ScalarVector(0.0, 1.0, 0.0)), 1E-6)
  }

  @Test
  fun testMag() {
    assertEquals(sqrt(27.0), ScalarVector.of(3.0, 3).mag(), 1E-6)
  }

  @Test
  fun testHashCode() {
    assertTrue(ScalarVector.of(3.0, 4).hashCode() == ScalarVector.of(3.0, 4).hashCode())
  }

  @Test
  fun testDist() {
    assertEquals(sqrt(2.0), ScalarVector(1.0, 1.0).dist(ScalarVector(0.0, 0.0)), 1E-5)
    assertEquals(2.0, ScalarVector(1.0, 1.0).dist2(ScalarVector(0.0, 0.0)), 1E-5)
  }

  @Test
  fun testTruncate() {
    assertEquals(ScalarVector(1.0, 1.0, 1.0), ScalarVector(1.0, 1.0, 1.0, 2.0, 2.0, 2.0).truncateElement(2.0))
  }

  @Test
  fun testAssertSameDim() {
    ScalarVector.assertSameDim(listOf(ScalarVector(1.0, 1.0, 1.0), ScalarVector(1.0, 2.0, 3.0), ScalarVector(0.0, 0.0, 0.0)))
  }
}
