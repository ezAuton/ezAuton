package com.github.ezauton.core.math

import com.github.ezauton.core.trajectory.geometry.ImmutableVector
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class VectorTest {

    @Test
    fun testVectorConstructors() {
        assertNotEquals(ImmutableVector(1), ImmutableVector(1, 1))
    }

    @Test
    fun testAccessingComponents() {
        val a = ImmutableVector(123, 155)

        assertEquals(123.0, a.get(0), DELTA)
        assertEquals(123.0, a.get(0), DELTA)
        assertEquals(155.0, a.get(1), DELTA)
        assertEquals(155.0, a.get(1), DELTA)
    }

    @Test
    fun testOnly2Components() {
        val a = ImmutableVector(123, 155)
        assertThrows(IndexOutOfBoundsException::class.java) { a.get(2) }
    }

    @Test
    fun testPerpendicular() {
        assertEquals(j.mul(-1.0), MathUtils.perp(i))
        assertNotEquals(i, MathUtils.perp(i))
        assertEquals(origin, MathUtils.perp(origin))

        assertEquals(ImmutableVector(0, 0, 1), MathUtils.cross(ImmutableVector(1, 0, 0), ImmutableVector(0, 1, 0)))
    }

    companion object {
        private val DELTA = 1E-5
        private val i = ImmutableVector(1, 0)
        private val j = ImmutableVector(0, 1)
        private val origin = ImmutableVector(0, 0)
    }
}
