package com.github.ezauton.core.math

import com.github.ezauton.conversion.ScalarVector
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class LineTest {
    private val DELTA = 1e-5
    private val horizontal = MathUtils.Geometry.LineR2(ScalarVector(0, 0), ScalarVector(1, 0))
    private val vertical = MathUtils.Geometry.LineR2(ScalarVector(0, 0), ScalarVector(0, 1))
    private val diag = MathUtils.Geometry.LineR2(ScalarVector(0, 0), ScalarVector(1, 1))
    private val otherDiag = MathUtils.Geometry.LineR2(ScalarVector(0, 0), ScalarVector(-1, 1))

    @Test
    fun testEvaluateY() {
        assertEquals(horizontal.evaluateY(1.0), 0.0, DELTA)
        assertEquals(diag.evaluateY(1.0), 1.0, DELTA)

        for (i in 0..19) {
            val ax = (Math.random() - 0.5) * 20
            val ay = (Math.random() - 0.5) * 20
            val a = ScalarVector(ax, ay)

            val bx = (Math.random() - 0.5) * 20
            val by = (Math.random() - 0.5) * 20
            val b = ScalarVector(bx, by)

            val line = MathUtils.Geometry.LineR2(a, b)

            assertEquals(line.evaluateY(ax), ay, DELTA)
            assertEquals(line.evaluateY(bx), by, DELTA)
        }
    }

    @Test
    fun testIntersect() {

        for (i in 0..19) {
            val ax = (Math.random() - 0.5) * 20
            val ay = (Math.random() - 0.5) * 20
            val a = ScalarVector(ax, ay)

            val bx = (Math.random() - 0.5) * 20
            val by = (Math.random() - 0.5) * 20
            val b = ScalarVector(bx, by)

            val cx = (Math.random() - 0.5) * 20
            val cy = (Math.random() - 0.5) * 20
            val c = ScalarVector(cx, cy)

            val lineAB = MathUtils.Geometry.LineR2(a, b)
            val lineBC = MathUtils.Geometry.LineR2(b, c)

            assertEquals(b, lineAB.intersection(lineBC))
            assertEquals(b, lineBC.intersection(lineAB))
        }

        assertNull(horizontal.intersection(horizontal))
    }

    @Test
    fun testLineEquals() {
        val notALine = ""
        assertNotEquals(horizontal, notALine)
        assertEquals(horizontal, horizontal)

        assertNotEquals(horizontal, vertical)
    }

    //    @Test //TODO: fix
    fun testPerp() {
        assertEquals(otherDiag, diag.getPerp(ScalarVector(0, 0)))
        assertEquals(diag, otherDiag.getPerp(ScalarVector(0, 0)))

        assertEquals(horizontal, vertical.getPerp(ScalarVector(0, 0)))
        assertEquals(vertical,
                horizontal.getPerp(ScalarVector(0, 0))
        )
    }

    @Test
    fun testIntegrate() {
        assertEquals(0.5, diag.integrate(0.0, 1.0), DELTA)
        assertEquals(0.5, diag.integrate(), DELTA)
        assertEquals(0.0, diag.integrate(-1.0, 1.0), DELTA)
    }
}
