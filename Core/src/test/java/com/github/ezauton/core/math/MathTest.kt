package com.github.ezauton.core.math

import com.github.ezauton.core.pathplanning.LinearPathSegment
import com.github.ezauton.core.trajectory.geometry.ImmutableVector
import com.github.ezauton.core.utils.MathUtils
import com.google.common.collect.ImmutableMap
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class MathTest {
    private val DELTA = 1E-5
    private val e1 = ImmutableVector(1, 0)

    init {
        MathUtils.init()
    }

    @Test
    fun test1() {
        // robot Location ImmutableVector{elements=[-0.008823892537926835, 8.750675036640743]}
        val robotPos = ImmutableVector(-0.008823892537926835, 8.750675036640743)
        val pathSegment = object : LinearPathSegment(ImmutableVector(-0.5, 8.589), ImmutableVector(-0.5, 12.405), false, false, 0.0) {
            override fun getSpeed(absoluteDistance: Double): Double {
                return 0.0
            }
        }

        // latest closest point ImmutableVector{elements=[-0.5116462707519531, 8.695889472961426]}

        val closestPoint = pathSegment.getClosestPoint(robotPos) // ImmutableVector{elements=[-0.5, 8.644769668579102]}
        //        System.out.println(closestPoint);
        val absoluteDistance = pathSegment.getAbsoluteDistance(closestPoint)

        //        System.out.println(absoluteDistance);


        // path = PathSegment{from=ImmutableVector{elements=[-0.5, 8.589]}, to=ImmutableVector{elements=[-0.5, 12.405]}}
        // getClosestPoint() ==> distance along
    }

    @Test
    fun testRotation90() {
        val rotated90 = MathUtils.LinearAlgebra.rotate2D(e1, Math.PI / 2)

        assertEquals(0.0, rotated90.get(0), 0.001)
        assertEquals(1.0, rotated90.get(1), 0.001)
    }

    @Test
    fun testRotation720() {
        val rotated720 = MathUtils.LinearAlgebra.rotate2D(e1, Math.PI * 2)

        assertEquals(1.0, rotated720.get(0), 0.001)
        assertEquals(0.0, rotated720.get(1), 0.001)
    }

    @Test
    fun testPosRotationCoordinateTransform() {
        val robotLocation = ImmutableVector(1, 1)
        val robotHeading = 7f * Math.PI / 4
        val absoluteCoord = ImmutableVector(2, 2)

        val distance = robotLocation.dist(absoluteCoord)

        val relativeCoord = MathUtils.LinearAlgebra.absoluteToRelativeCoord(absoluteCoord, robotLocation, robotHeading)
        val absCoord = MathUtils.LinearAlgebra.relativeToAbsoluteCoord(relativeCoord, robotLocation, robotHeading)

        assertEquals(0.0, relativeCoord.get(0), 0.001)
        assertEquals(distance, relativeCoord.get(1), 0.001)
        assertEquals(absoluteCoord, absCoord)
    }

    @Test
    fun testNegRotationCoordinateTransform() {
        val robotLocation = ImmutableVector(1, 1)
        val robotHeading = -Math.PI / 4
        val absoluteCoord = ImmutableVector(2, 2)

        val distance = robotLocation.dist(absoluteCoord)

        val relativeCoord = MathUtils.LinearAlgebra.absoluteToRelativeCoord(absoluteCoord, robotLocation, robotHeading)
        val absCoord = MathUtils.LinearAlgebra.relativeToAbsoluteCoord(relativeCoord, robotLocation, robotHeading)

        assertEquals(0.0, relativeCoord.get(0), 0.001)
        assertEquals(distance, relativeCoord.get(1), 0.001)
        assertEquals(0.0, absoluteCoord.sub(absCoord).mag2(), 1E-7)
    }


    /**
     * Should be a complete rotation around a circle (dpos = 0)
     */
    @Test
    fun testDTheta90() {
        val dTheta = MathUtils.Geometry.getDThetaNavX(270.0, 0.0)
        assertEquals(3f * Math.PI / 2f, dTheta, 0.001)
    }

    /**
     * Should be a complete rotation around a circle (dpos = 0)
     */
    @Test
    fun testIsCCWClosest() {
        assertFalse(MathUtils.Geometry.isCCWQuickest(0.0, 90.0))
        assertTrue(MathUtils.Geometry.isCCWQuickest(90.0, 0.0))
        assertTrue(MathUtils.Geometry.isCCWQuickest(127.0, 359.0))
        assertFalse(MathUtils.Geometry.isCCWQuickest(355.0, 5.0)) // 359 is at 11:59, 2 is at 12:01
    }

    @Test
    fun testDAngle() {
        for (i in 0..9) {
            val a = Math.random() * 360
            assertEquals(0.0, MathUtils.Geometry.getDAngle(a, a), DELTA)
        }

        assertEquals(90.0, MathUtils.Geometry.getDAngle(90.0, 180.0), DELTA)
        assertEquals(90.0, MathUtils.Geometry.getDAngle(180.0, 90.0), DELTA)
        assertEquals(90.0, MathUtils.Geometry.getDAngle(180.0, -90.0), DELTA)
        assertEquals(180.0, MathUtils.Geometry.getDAngle(135.0, -45.0), DELTA)
        assertEquals(90.0, MathUtils.Geometry.getDAngle(135.0, 45.0), DELTA)
    }

    @Test
    fun testSignSame() {
        assertTrue(MathUtils.signSame(java.lang.Double.MAX_VALUE, java.lang.Double.MAX_VALUE))
        assertTrue(MathUtils.signSame(1234.4, 1234.1))
        assertTrue(MathUtils.signSame(1234.4, 1234.4))
        assertTrue(MathUtils.signSame(1 / 3.0, 1 / 3.0))
        assertTrue(MathUtils.signSame(0.0, 0.0))
        assertFalse(MathUtils.signSame(-1234.4, 1234.1))
        assertFalse(MathUtils.signSame(-1234.4, 1234.4))
        assertFalse(MathUtils.signSame(-1 / 3.0, 1 / 3.0))
    }

    @Test
    fun testMinAbs() {

        assertEquals(MathUtils.minAbs(3.0, 5.0), 3.0, DELTA)
        assertEquals(MathUtils.minAbs(3.0, -5.0), 3.0, DELTA)
        assertEquals(MathUtils.minAbs(-3.0, -5.0), -3.0, DELTA)
        assertEquals(MathUtils.minAbs(-3.0, -5 / 3.0), -5 / 3.0, DELTA)
        assertEquals(MathUtils.minAbs(-3.0, 3.0), -3.0, DELTA)
        assertEquals(MathUtils.minAbs(3.0, -3.0), 3.0, DELTA)
    }

    @Test
    fun testMaxAbs() {

        assertEquals(MathUtils.maxAbs(3.0, 5.0), 5.0, DELTA)
        assertEquals(MathUtils.maxAbs(3.0, -5.0), -5.0, DELTA)
        assertEquals(MathUtils.maxAbs(-3.0, -5.0), -5.0, DELTA)
        assertEquals(MathUtils.maxAbs(-3.0, -5 / 3.0), -3.0, DELTA)
        assertEquals(MathUtils.maxAbs(-3.0, 3.0), -3.0, DELTA)
        assertEquals(MathUtils.maxAbs(3.0, -3.0), 3.0, DELTA)
    }

    @Test
    fun testDegToRad() {
        for (i in 0..19) {
            val deg = Math.random() * 360
            val rad = MathUtils.deg2Rad(deg)

            assertEquals(rad, Math.toRadians(deg), DELTA)
        }
    }

    @Test
    fun testRadToDeg() {
        for (i in 0..19) {
            val rad = Math.random() * 360
            val deg = MathUtils.rad2Deg(rad)

            assertEquals(deg, Math.toDegrees(rad), DELTA)
        }
    }

    @Test
    fun testEpsilonEqualsNumbers() {
        for (i in 0..19) {
            val a = Math.random() * 360
            val b = Math.sqrt(a * a) * 3.0 / 3 + 1.987 - 1.0 - 0.987 // try to accumulate FP errors

            assertTrue(MathUtils.epsilonEquals(a, b))
            assertTrue(MathUtils.epsilonEquals(a.toFloat().toDouble(), b.toFloat().toDouble()))
        }
    }

    @Test
    fun testEpsilonEqualsVectors() {
        for (i in 0..19) {
            val ax = Math.random() * 360
            val bx = Math.sqrt(ax * ax) * 3.0 / 3 + 1.987 - 1.0 - 0.987 // try to accumulate FP errors

            val ay = Math.random() * 360
            val by = Math.sqrt(ay * ay) * 3.0 / 3 + 1.987 - 1.0 - 0.987 // try to accumulate FP errors

            val vecA = ImmutableVector(ax, ay)
            val vecB = ImmutableVector(bx, by)

            assertEquals(vecA, vecB)
            assertTrue(MathUtils.epsilonEquals(vecA, vecB))
        }
    }

    @Test
    fun testDecimalComponent() {
        assertEquals(.567, MathUtils.decimalComponent(1234.567), 1E-6)
    }

    @Test
    fun testFloor() {
        for (i in 0..19) {
            val a = (Math.random() - 0.5) * 2.0 * 360.0
            assertEquals(MathUtils.floor(a).toDouble(), Math.floor(a), DELTA)
            assertEquals(MathUtils.lfloor(a).toDouble(), Math.floor(a), DELTA)
            assertEquals(MathUtils.floor(a.toFloat().toDouble()).toDouble(), Math.floor(a.toFloat().toDouble()), DELTA)
        }
    }

    @Test
    fun testShiftRadiansBounded() {
        assertEquals(Math.PI, MathUtils.shiftRadiansBounded(0.0, Math.PI), DELTA)
        assertEquals(0.0, MathUtils.shiftRadiansBounded(0.0, MathUtils.TAU), DELTA)
        assertEquals(Math.PI, MathUtils.shiftRadiansBounded(Math.PI / 2, Math.PI / 2), DELTA)
        assertEquals(0.0, MathUtils.shiftRadiansBounded(Math.PI / 2, 3 * Math.PI / 2), DELTA)

    }

    @Test
    fun testBetweenVec() {
        for (i in 0..39) {
            val ax = Math.random() * 10
            val ay = Math.random() * 10
            val a = ImmutableVector(ax, ay)

            val bx = Math.random() * 10 + 10
            val by = Math.random() * 10 + 10
            val b = ImmutableVector(bx, by)

            val cx = (ax + bx) / 2
            val cy = (ay + by) / 2
            val c1 = ImmutableVector(cx, cy)

            val c2 = ImmutableVector(ax, cy)
            val c3 = ImmutableVector(cx, ay)
            val c4 = ImmutableVector(bx, cy)
            val c5 = ImmutableVector(cx, by)
            assertTrue(MathUtils.between(a, c1, b))
            assertTrue(MathUtils.between(a, c2, b))
            assertTrue(MathUtils.between(a, c3, b))
            assertTrue(MathUtils.between(a, c4, b))
            assertTrue(MathUtils.between(a, c5, b))
        }
    }

    @Test
    fun testLogarithms() {
        for (i in 0..39) {
            val a = Math.random() * 360

            assertEquals(Math.log(a) / Math.log(2.0), MathUtils.log2(a), DELTA)
            assertEquals(Math.log(a) / Math.log(3.0), MathUtils.log3(a), DELTA)
            assertEquals(Math.log(a) / Math.log(4.0), MathUtils.log4(a), DELTA)
            assertEquals(Math.log(a) / Math.log(5.0), MathUtils.log5(a), DELTA)
            assertEquals(Math.log(a) / Math.log(6.0), MathUtils.log6(a), DELTA)
            assertEquals(Math.log(a) / Math.log(7.0), MathUtils.log7(a), DELTA)
            assertEquals(Math.log(a) / Math.log(8.0), MathUtils.log8(a), DELTA)
            assertEquals(Math.log(a) / Math.log(9.0), MathUtils.log9(a), DELTA)
            assertEquals(Math.log(a) / Math.log(10.0), MathUtils.log10(a), DELTA)

            for (j in 1..9) {
                assertEquals(Math.log(a) / Math.log(j.toDouble()), MathUtils.log(j.toDouble(), a), DELTA)
            }

            assertEquals(StrictMath.log(a), MathUtils.ln(a), DELTA)
        }
    }

    @Test
    fun testPow() {
        for (i in 0..39) {
            val a = Math.random() * 15

            assertEquals(Math.pow(a, 2.0), MathUtils.pow2(a), DELTA)
            assertEquals(Math.pow(a, 3.0), MathUtils.pow3(a), DELTA)
            assertEquals(Math.pow(a, 4.0), MathUtils.pow4(a), DELTA)
            assertEquals(Math.pow(a, 5.0), MathUtils.pow5(a), DELTA)
            assertEquals(Math.pow(a, 6.0), MathUtils.pow6(a), DELTA)
            assertEquals(Math.pow(a, 7.0), MathUtils.pow7(a), DELTA)
            assertEquals(Math.pow(a, 8.0), MathUtils.pow8(a), 1E-4)
            assertEquals(Math.pow(a, 9.0), MathUtils.pow9(a), 1E-3)
            assertEquals(Math.pow(a, 10.0), MathUtils.pow10(a), 1E-2)
        }
    }

    @Test
    fun testQuadratic() {
        for (i in -20..19) {
            for (j in -20..19) {
                val quadratic = { x -> x * x - i * x - j * x + i * j }
                val solutions = MathUtils.Algebra.quadratic(1.0, (-i - j).toDouble(), (i * j).toDouble())

                for (solution in solutions) {
                    assertEquals(0.0, quadratic.get(solution), DELTA)
                }
            }
        }

        val solutions = MathUtils.Algebra.quadratic(1.0, 0.0, 1.0)
        assertEquals(0, solutions.size)
    }

    //    @Test
    //    public void testCircleLineIntersection()
    //    {
    //        ImmutableVector i = new ImmutableVector(1, 0);
    //        ImmutableVector j = new ImmutableVector(0, 1);
    //        ImmutableVector origin = new ImmutableVector(0, 0);
    //        MathUtils.Geometry.Line horizontal = new MathUtils.Geometry.Line(origin, i);
    //        MathUtils.Geometry.Line vertical = new MathUtils.Geometry.Line(origin, j);
    //
    //        assertArrayEquals(MathUtils.Geometry.getCircleLineIntersectionPoint(horizontal, origin, 1), new ImmutableVector[] { i.mul(-1), i });
    //        assertArrayEquals(MathUtils.Geometry.getCircleLineIntersectionPoint(vertical, origin, 1), new ImmutableVector[] { j.mul(-1), j });
    //    }

    @Test
    fun testAngleFromPoints() {
        val i = ImmutableVector(1, 0)
        val j = ImmutableVector(0, 1)
        val diag = ImmutableVector(1, 1)
        val origin = ImmutableVector(0, 0)

        assertEquals(3 * Math.PI / 4, MathUtils.Geometry.getThetaFromPoints(i, j), DELTA)
        assertEquals(-Math.PI / 4, MathUtils.Geometry.getThetaFromPoints(i.mul(-1.0), j.mul(-1.0)), DELTA)
        assertEquals(0.0, MathUtils.Geometry.getThetaFromPoints(j, diag), DELTA) // line from j to diag is flat

    }

    @Test
    fun testMin() {
        for (i in 0..9) {
            val a = (Math.random() - 0.5) * 20
            val b = (Math.random() - 0.5) * 20
            val c = (Math.random() - 0.5) * 20
            val d = (Math.random() - 0.5) * 20
            val e = (Math.random() - 0.5) * 20

            assertEquals(Math.min(a, Math.min(b, Math.min(c, Math.min(d, e)))), MathUtils.min(a, b, c, d, e), DELTA)
        }
    }

    //    @Test //TODO: fix
    fun testClosestPointOnLine() {
        val robotPos = ImmutableVector(0, 0)

        val testCases = arrayOf(arrayOf(ImmutableVector(1, 1), ImmutableVector(3, 3)), // point a should be closest
                arrayOf(ImmutableVector(-1, -1), ImmutableVector(1, 1)), arrayOf(ImmutableVector(-5, -5), ImmutableVector(-3, -3)), arrayOf(ImmutableVector(-1, 0), ImmutableVector(1, 2)))

        vectorsCloseEnough(MathUtils.Geometry.getClosestPointLineSegments(testCases[0][0], testCases[0][1], robotPos), testCases[0][0])
        vectorsCloseEnough(MathUtils.Geometry.getClosestPointLineSegments(testCases[1][0], testCases[1][1], robotPos), robotPos)

        vectorsCloseEnough(MathUtils.Geometry.getClosestPointLineSegments(testCases[2][0], testCases[2][1], robotPos), testCases[2][1])

        val segment = MathUtils.Geometry.LineR2(testCases[3][0], testCases[3][1])
        val perp = segment.getPerp(robotPos)
        vectorsCloseEnough(MathUtils.Geometry.getClosestPointLineSegments(testCases[3][0], testCases[3][1], robotPos), segment.intersection(perp))


    }

    @Test
    fun testEvenFunc() {
        var map: Map<Double, Double> = ImmutableMap.builder<Double, Double>()
                .put(-1.0, 0.0)
                .put(1.0, 0.0)
                .build()
        assertTrue(MathUtils.Algebra.hasEvenSymmetry(map))

        map = ImmutableMap.builder<Double, Double>()
                .put(1.0, 0.0)
                .put(2.0, 0.0)
                .build()
        assertFalse(MathUtils.Algebra.hasEvenSymmetry(map))

        map = ImmutableMap.builder<Double, Double>()
                .put(-1.0, 0.0)
                .put(-2.0, 0.0)
                .build()
        assertFalse(MathUtils.Algebra.hasEvenSymmetry(map))

        map = ImmutableMap.builder<Double, Double>()
                .put(-1.0, 0.0)
                .put(-2.0, 0.0)
                .put(1.0, 1.0)
                .put(2.0, 0.0)
                .build()
        assertFalse(MathUtils.Algebra.hasEvenSymmetry(map))

        map = ImmutableMap.builder<Double, Double>()
                .put(-1.0, 1.0)
                .put(-2.0, 0.0)
                .put(1.0, 0.0)
                .put(2.0, 0.0)
                .build()
        assertFalse(MathUtils.Algebra.hasEvenSymmetry(map))

        map = ImmutableMap.builder<Double, Double>()
                .put(-1.0, 1.0)
                .put(-2.0, 0.0)
                .put(1.0, 1.0)
                .put(2.0, 0.0)
                .build()

        assertTrue(MathUtils.Algebra.hasEvenSymmetry(map))
    }

    @Test
    fun testOddFunc() {
        var map: Map<Double, Double> = ImmutableMap.builder<Double, Double>()
                .put(-1.0, 0.0)
                .put(1.0, 0.0)
                .build()
        assertTrue(MathUtils.Algebra.hasOddSymmetry(map))

        map = ImmutableMap.builder<Double, Double>()
                .put(1.0, 0.0)
                .put(2.0, 0.0)
                .build()
        assertFalse(MathUtils.Algebra.hasOddSymmetry(map))

        map = ImmutableMap.builder<Double, Double>()
                .put(-1.0, 0.0)
                .put(-2.0, 0.0)
                .build()
        assertFalse(MathUtils.Algebra.hasOddSymmetry(map))

        map = ImmutableMap.builder<Double, Double>()
                .put(-1.0, 0.0)
                .put(-2.0, 0.0)
                .put(1.0, 1.0)
                .put(2.0, 0.0)
                .build()
        assertFalse(MathUtils.Algebra.hasOddSymmetry(map))

        map = ImmutableMap.builder<Double, Double>()
                .put(-1.0, 1.0)
                .put(-2.0, 0.0)
                .put(1.0, 0.0)
                .put(2.0, 0.0)
                .build()
        assertFalse(MathUtils.Algebra.hasOddSymmetry(map))

        map = ImmutableMap.builder<Double, Double>()
                .put(-1.0, 1.0)
                .put(-2.0, 0.0)
                .put(1.0, -1.0)
                .put(2.0, 0.0)
                .build()

        assertTrue(MathUtils.Algebra.hasOddSymmetry(map))
    }


    private fun vectorsCloseEnough(a: ImmutableVector, b: ImmutableVector?) {
        assertTrue(MathUtils.epsilonEquals(a, b!!, 1E-3))
    }

}
