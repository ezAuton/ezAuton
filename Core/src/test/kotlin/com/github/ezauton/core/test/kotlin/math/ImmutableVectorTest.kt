package com.github.ezauton.core.test.kotlin.math

import com.github.ezauton.core.trajectory.geometry.ImmutableVector
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

class ImmutableVectorTest {


    @Test
    fun testWrongSize() {
        assertThrows<IllegalArgumentException>(IllegalArgumentException::class.java) { ImmutableVector(1.0, 1.0).assertSize(1) }
    }

    fun testCollectionWrongSize() {
        val oddOneOut = ImmutableVector(Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0))

        val vectors = ArrayList<ImmutableVector>()
        for (i in 0..4) {
            vectors.add(ImmutableVector(1.0, 2.0, 3.0, 4.0))
        }
        vectors.add(oddOneOut)

        assertThrows(IllegalArgumentException::class.java){ImmutableVector.assertSameDim(vectors)}
    }

    @Test
    fun testEq() {
        assertEquals(ImmutableVector(1.0, 1.0), ImmutableVector(1.0, 1.0))
        assertNotEquals(ImmutableVector(1.0, 1.0), ImmutableVector(2.0, 1.0))
    }

    @Test
    fun testOf() {
        assertEquals(ImmutableVector(4.0, 4.0, 4.0), ImmutableVector.of(4.0, 3))
    }

    @Test
    fun testMultiply() {
        assertEquals(ImmutableVector(3.0, 6.0, 9.0), ImmutableVector(1.0, 2.0, 3.0).mul(3.0))
    }

    @Test
    fun testDot() {
        assertEquals(0.0, ImmutableVector(1.0, 0.0, 0.0).dot(ImmutableVector(0.0, 1.0, 0.0)), 1E-6)
    }

    @Test
    fun testMag() {
        assertEquals(Math.sqrt(27.0), ImmutableVector.of(3.0, 3).mag(), 1E-6)
    }

    @Test
    fun testHashCode() {
        assertTrue(ImmutableVector.of(3.0, 4).hashCode() == ImmutableVector.of(3.0, 4).hashCode())
    }

    @Test
    fun testDist() {
        assertEquals(Math.sqrt(2.0), ImmutableVector(1.0, 1.0).dist(ImmutableVector(0.0, 0.0)), 1E-5)
        assertEquals(2.0, ImmutableVector(1.0, 1.0).dist2(ImmutableVector(0.0, 0.0)), 1E-5)
    }

    @Test
    fun testTruncate() {
        assertEquals(ImmutableVector(1.0, 1.0, 1.0), ImmutableVector(1.0, 1.0, 1.0, 2.0, 2.0, 2.0).truncateElement(2.0))
    }

    @Test
    fun testAssertSameDim() {
        ImmutableVector.assertSameDim(Arrays.asList(ImmutableVector(1.0, 1.0, 1.0), ImmutableVector(1.0, 2.0, 3.0), ImmutableVector(0.0, 0.0, 0.0)))
    }
}
