package com.team2502.ezauton.test.math;

import com.team2502.ezauton.utils.MathUtils;
import org.joml.ImmutableVector;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

public class MathTest
{
    private ImmutableVector e1 = new ImmutableVector(1, 0);

    private final double DELTA = 1E-5;

    public MathTest()
    {
        MathUtils.init();
    }

    @Test
    public void testRotation90()
    {
        ImmutableVector rotated90 = MathUtils.LinearAlgebra.rotate2D(e1, Math.PI / 2);

        assertEquals(0, rotated90.x, 0.001);
        assertEquals(1, rotated90.y, 0.001);
    }

    @Test
    public void testRotation720()
    {
        ImmutableVector rotated720 = MathUtils.LinearAlgebra.rotate2D(e1, Math.PI * 2);

        assertEquals(1, rotated720.x, 0.001);
        assertEquals(0, rotated720.y, 0.001);
    }

    @Test
    public void testPosRotationCoordinateTransform()
    {
        ImmutableVector robotLocation = new ImmutableVector(1, 1);
        double robotHeading = 7F * Math.PI / 4;
        ImmutableVector absoluteCoord = new ImmutableVector(2, 2);

        double distance = robotLocation.distance(absoluteCoord);

        ImmutableVector relativeCoord = MathUtils.LinearAlgebra.absoluteToRelativeCoord(absoluteCoord, robotLocation, robotHeading);

        assertEquals(0, relativeCoord.x, 0.001);
        assertEquals(distance, relativeCoord.y, 0.001);
    }

    @Test
    public void testNegRotationCoordinateTransform()
    {
        ImmutableVector robotLocation = new ImmutableVector(1, 1);
        double robotHeading = -Math.PI / 4;
        ImmutableVector absoluteCoord = new ImmutableVector(2, 2);

        double distance = robotLocation.distance(absoluteCoord);

        ImmutableVector relativeCoord = MathUtils.LinearAlgebra.absoluteToRelativeCoord(absoluteCoord, robotLocation, robotHeading);

        assertEquals(0, relativeCoord.x, 0.001);
        assertEquals(distance, relativeCoord.y, 0.001);
    }


    /**
     * Should be a complete rotation around a circle (dpos = 0)
     */
    @Test
    public void testDTheta90()
    {
        double dTheta = MathUtils.Geometry.getDThetaNavX(270, 0);
        assertEquals(3F * Math.PI / 2F, dTheta, 0.001);
    }

    /**
     * Should be a complete rotation around a circle (dpos = 0)
     */
    @Test
    public void testIsCCWClosest()
    {
        assertFalse(MathUtils.Geometry.isCCWQuickest(0, 90));
        assertTrue(MathUtils.Geometry.isCCWQuickest(90, 0));
        assertTrue(MathUtils.Geometry.isCCWQuickest(127, 359));
        assertFalse(MathUtils.Geometry.isCCWQuickest(355, 5)); // 359 is at 11:59, 2 is at 12:01
    }

    @Test
    public void testDAngle()
    {
        for(int i = 0; i < 10; i++)
        {
            double a = Math.random() * 360;
            assertEquals(0, MathUtils.Geometry.getDAngle(a, a), DELTA);
        }

        assertEquals(90, MathUtils.Geometry.getDAngle(90, 180), DELTA);
        assertEquals(90, MathUtils.Geometry.getDAngle(180, 90), DELTA);
        assertEquals(90, MathUtils.Geometry.getDAngle(180, -90), DELTA);
        assertEquals(180, MathUtils.Geometry.getDAngle(135, -45), DELTA);
        assertEquals(90, MathUtils.Geometry.getDAngle(135, 45), DELTA);
    }

    @Test
    public void testSignSame()
    {
        assertTrue(MathUtils.signSame(Double.MAX_VALUE, Double.MAX_VALUE));
        assertTrue(MathUtils.signSame(1234.4, 1234.1));
        assertTrue(MathUtils.signSame(1234.4, 1234.4));
        assertTrue(MathUtils.signSame(1 / 3.0, 1 / 3.0));
        assertTrue(MathUtils.signSame(0, 0));
        assertFalse(MathUtils.signSame(-1234.4, 1234.1));
        assertFalse(MathUtils.signSame(-1234.4, 1234.4));
        assertFalse(MathUtils.signSame(-1 / 3.0, 1 / 3.0));
    }

    @Test
    public void testMinAbs()
    {

        assertEquals(MathUtils.minAbs(3, 5), 3, DELTA);
        assertEquals(MathUtils.minAbs(3, -5), 3, DELTA);
        assertEquals(MathUtils.minAbs(-3, -5), -3, DELTA);
        assertEquals(MathUtils.minAbs(-3, -5 / 3.0), -5 / 3.0, DELTA);
        assertEquals(MathUtils.minAbs(-3, 3), -3, DELTA);
        assertEquals(MathUtils.minAbs(3, -3), 3, DELTA);
    }

    @Test
    public void testMaxAbs()
    {

        assertEquals(MathUtils.maxAbs(3, 5), 5, DELTA);
        assertEquals(MathUtils.maxAbs(3, -5), -5, DELTA);
        assertEquals(MathUtils.maxAbs(-3, -5), -5, DELTA);
        assertEquals(MathUtils.maxAbs(-3, -5 / 3.0), -3, DELTA);
        assertEquals(MathUtils.maxAbs(-3, 3), -3, DELTA);
        assertEquals(MathUtils.maxAbs(3, -3), 3, DELTA);
    }

    @Test
    public void testDegToRad()
    {
        for(int i = 0; i < 20; i++)
        {
            double deg = Math.random() * 360;
            double rad = MathUtils.deg2Rad(deg);

            assertEquals(rad, Math.toRadians(deg), DELTA);
        }
    }

    @Test
    public void testRadToDeg()
    {
        for(int i = 0; i < 20; i++)
        {
            double rad = Math.random() * 360;
            double deg = MathUtils.rad2Deg(rad);

            assertEquals(deg, Math.toDegrees(rad), DELTA);
        }
    }

    @Test
    public void testEpsilonEqualsNumbers()
    {
        for(int i = 0; i < 20; i++)
        {
            double a = Math.random() * 360;
            double b = (Math.sqrt(a * a) * 3.0) / 3 + 1.987 - 1 - 0.987; // try to accumulate FP errors

            assertTrue(MathUtils.epsilonEquals(a, b));
            assertTrue(MathUtils.epsilonEquals((float) a, (float) b));
        }
    }

    @Test
    public void testEpsilonEqualsVectors()
    {
        for(int i = 0; i < 20; i++)
        {
            double ax = Math.random() * 360;
            double bx = (Math.sqrt(ax * ax) * 3.0) / 3 + 1.987 - 1 - 0.987; // try to accumulate FP errors

            double ay = Math.random() * 360;
            double by = (Math.sqrt(ay * ay) * 3.0) / 3 + 1.987 - 1 - 0.987; // try to accumulate FP errors

            ImmutableVector vecA = new ImmutableVector(ax, ay);
            ImmutableVector vecB = new ImmutableVector(bx, by);

            assertEquals(vecA, vecB);
            assertTrue(MathUtils.epsilonEquals(vecA, vecB));
        }
    }

    @Test
    public void testFloor()
    {
        for(int i = 0; i < 20; i++)
        {
            double a = (Math.random() - 0.5) * 2 * 360;
            assertEquals(MathUtils.floor(a), Math.floor(a), DELTA);
            assertEquals(MathUtils.lfloor(a), Math.floor(a), DELTA);
            assertEquals(MathUtils.floor((float) a), Math.floor((float) a), DELTA);
        }
    }

    @Test
    public void testShiftRadiansBounded()
    {
        assertEquals(Math.PI, MathUtils.shiftRadiansBounded(0, Math.PI), DELTA);
        assertEquals(0, MathUtils.shiftRadiansBounded(0, MathUtils.TAU), DELTA);
        assertEquals(Math.PI, MathUtils.shiftRadiansBounded(Math.PI / 2, Math.PI / 2), DELTA);
        assertEquals(0, MathUtils.shiftRadiansBounded(Math.PI / 2, 3 * Math.PI / 2), DELTA);

    }

    @Test
    public void testBetweenVec()
    {
        for(int i = 0; i < 40; i++)
        {
            double ax = (Math.random()) * 10;
            double ay = (Math.random()) * 10;
            ImmutableVector a = new ImmutableVector(ax, ay);

            double bx = (Math.random() * 10) + 10;
            double by = (Math.random() * 10) + 10;
            ImmutableVector b = new ImmutableVector(bx, by);

            double cx = (ax + bx) / (2);
            double cy = (ay + by) / (2);
            ImmutableVector c1 = new ImmutableVector(cx, cy);

            ImmutableVector c2 = new ImmutableVector(ax, cy);
            ImmutableVector c3 = new ImmutableVector(cx, ay);
            ImmutableVector c4 = new ImmutableVector(bx, cy);
            ImmutableVector c5 = new ImmutableVector(cx, by);
            assertTrue("A: " + a + " B: " + b + " C: " + c1, MathUtils.between(a, c1, b));
            assertTrue("A: " + a + " B: " + b + " C: " + c2, MathUtils.between(a, c2, b));
            assertTrue("A: " + a + " B: " + b + " C: " + c3, MathUtils.between(a, c3, b));
            assertTrue("A: " + a + " B: " + b + " C: " + c4, MathUtils.between(a, c4, b));
            assertTrue("A: " + a + " B: " + b + " C: " + c5, MathUtils.between(a, c5, b));
        }
    }

    @Test
    public void testLogarithms()
    {
        for(int i = 0; i < 40; i++)
        {
            double a = Math.random() * 360;

            assertEquals(Math.log(a) / Math.log(2), MathUtils.log2(a), DELTA);
            assertEquals(Math.log(a) / Math.log(3), MathUtils.log3(a), DELTA);
            assertEquals(Math.log(a) / Math.log(4), MathUtils.log4(a), DELTA);
            assertEquals(Math.log(a) / Math.log(5), MathUtils.log5(a), DELTA);
            assertEquals("A: " + a, Math.log(a) / Math.log(6), MathUtils.log6(a), DELTA);
            assertEquals(Math.log(a) / Math.log(7), MathUtils.log7(a), DELTA);
            assertEquals(Math.log(a) / Math.log(8), MathUtils.log8(a), DELTA);
            assertEquals(Math.log(a) / Math.log(9), MathUtils.log9(a), DELTA);
            assertEquals(Math.log(a) / Math.log(10), MathUtils.log10(a), DELTA);

            for(int j = 1; j < 10; j++)
            {
                assertEquals(Math.log(a) / Math.log(j), MathUtils.log(j, a), DELTA);
            }

            assertEquals(StrictMath.log(a), MathUtils.ln(a), DELTA);
        }
    }

    @Test
    public void testPow()
    {
        for(int i = 0; i < 40; i++)
        {
            double a = Math.random() * 15;

            assertEquals("a: " + a, Math.pow(a, 2), MathUtils.pow2(a), 1e2);
            assertEquals(Math.pow(a, 3), MathUtils.pow3(a), DELTA);
            assertEquals(Math.pow(a, 4), MathUtils.pow4(a), DELTA);
            assertEquals(Math.pow(a, 5), MathUtils.pow5(a), DELTA);
            assertEquals(Math.pow(a, 6), MathUtils.pow6(a), DELTA);
            assertEquals(Math.pow(a, 7), MathUtils.pow7(a), DELTA);
            assertEquals(Math.pow(a, 8), MathUtils.pow8(a), 1E-4);
            assertEquals(Math.pow(a, 9), MathUtils.pow9(a), 1E-3);
            assertEquals(Math.pow(a, 10), MathUtils.pow10(a), 1E-2);
        }
    }

    @Test
    public void testQuadratic()
    {
        for(int i = -20; i < 20; i++)
        {
            for(int j = -20; j < 20; j++)
            {
                final int iWrapper = i;
                final int jWrapper = j;
                MathUtils.Function quadratic = (x) -> x * x - iWrapper * x - jWrapper * x + iWrapper * jWrapper;
                Set<Double> solutions = MathUtils.Algebra.quadratic(1, -i - j, i * j);

                for(double solution : solutions)
                {
                    assertEquals(0, quadratic.get(solution), DELTA);
                }
            }
        }

        Set<Double> solutions = MathUtils.Algebra.quadratic(1, 0, 1);
        assertEquals(0, solutions.size());
    }

    @Test
    public void testCircleLineIntersection()
    {
        ImmutableVector i = new ImmutableVector(1, 0);
        ImmutableVector j = new ImmutableVector(0, 1);
        ImmutableVector origin = new ImmutableVector(0, 0);
        MathUtils.Geometry.Line horizontal = new MathUtils.Geometry.Line(origin, i);
        MathUtils.Geometry.Line vertical = new MathUtils.Geometry.Line(origin, j);

        assertArrayEquals(MathUtils.Geometry.getCircleLineIntersectionPoint(horizontal, origin, 1), new ImmutableVector[]{i.mul(-1), i});
        assertArrayEquals(MathUtils.Geometry.getCircleLineIntersectionPoint(vertical, origin, 1), new ImmutableVector[]{j.mul(-1), j});
    }

    @Test
    public void testAngleFromPoints()
    {
        ImmutableVector i = new ImmutableVector(1, 0);
        ImmutableVector j = new ImmutableVector(0, 1);
        ImmutableVector diag = new ImmutableVector(1, 1);
        ImmutableVector origin = new ImmutableVector(0, 0);

        assertEquals(3 * Math.PI / 4, MathUtils.Geometry.getThetaFromPoints(i, j), DELTA);
        assertEquals( -Math.PI / 4, MathUtils.Geometry.getThetaFromPoints(i.mul(-1), j.mul(-1)), DELTA);
        assertEquals(0, MathUtils.Geometry.getThetaFromPoints(j, diag), DELTA); // line from j to diag is flat

    }
    @Test
    public void testMin()
    {
        for(int i = 0; i < 10; i++)
        {
            double a = (Math.random() - 0.5) * 20;
            double b = (Math.random() - 0.5) * 20;
            double c = (Math.random() - 0.5) * 20;
            double d = (Math.random() - 0.5) * 20;
            double e = (Math.random() - 0.5) * 20;

            assertEquals(a + " " + b + " " + c + " " + d + " " + e, Math.min(a, Math.min(b, Math.min(c, Math.min(d, e)))), MathUtils.min(a, b, c, d, e), DELTA);
        }
    }

    @Test
    public void testClosestPointOnLine()
    {
        ImmutableVector robotPos = new ImmutableVector(0, 0);

        ImmutableVector[][] testCases = new ImmutableVector[][] {
                {new ImmutableVector(1, 1), new ImmutableVector(3, 3)}, // point a should be closest
                {new ImmutableVector(-1,  -1), new ImmutableVector(1, 1)},
                {new ImmutableVector(-5, -5), new ImmutableVector(-3, -3)},
                {new ImmutableVector(-1, 0), new ImmutableVector(1, 2)}
        };

        vectorsCloseEnough(MathUtils.Geometry.getClosestPointLineSegments(testCases[0][0], testCases[0][1], robotPos), testCases[0][0]);
        vectorsCloseEnough(MathUtils.Geometry.getClosestPointLineSegments(testCases[1][0], testCases[1][1], robotPos), robotPos);

        vectorsCloseEnough(MathUtils.Geometry.getClosestPointLineSegments(testCases[2][0], testCases[2][1], robotPos), testCases[2][1]);

        MathUtils.Geometry.Line segment = new MathUtils.Geometry.Line(testCases[3][0], testCases[3][1]);
        MathUtils.Geometry.Line perp = segment.getPerp(robotPos);
        vectorsCloseEnough(MathUtils.Geometry.getClosestPointLineSegments(testCases[3][0], testCases[3][1], robotPos), segment.intersection(perp));


    }


    private void vectorsCloseEnough(ImmutableVector a, ImmutableVector b)
    {
        Assert.assertTrue(a + " " + b, MathUtils.epsilonEquals(a, b, 1E-3));
    }

}
