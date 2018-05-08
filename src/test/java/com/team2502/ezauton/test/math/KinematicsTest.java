package com.team2502.ezauton.test.math;

import com.team2502.ezauton.test.utils.MathUtils;
import org.joml.ImmutableVector;
import org.junit.Assert;
import org.junit.Test;

import static com.team2502.ezauton.test.utils.MathUtils.ROOT_2;
import static org.junit.Assert.assertEquals;

public class KinematicsTest
{
    @Test
    public void testNavXBound()
    {
        assertEquals(355, MathUtils.Kinematics.navXBound(-5), 0.001);
        assertEquals(14, MathUtils.Kinematics.navXBound(14), 0.001);
    }

    @Test //fail
    public void testAbsoluteDPos45()
    {
        ImmutableVector dPos = MathUtils.Kinematics.getAbsoluteDPosLine(1, 1, 1F, (double) (Math.PI / 4F));

        assertEquals(Math.sqrt(1 / 2F), dPos.x, 0.001);
        assertEquals(Math.sqrt(1 / 2F), dPos.y, 0.001);
    }

    @Test
    public void navXToRad()
    {
        // TODO: returns cw radians not ccw I think
        double rad = MathUtils.Kinematics.navXToRad(270);

        assertEquals(Math.PI / 2F, rad, 0.001);

        rad = MathUtils.Kinematics.navXToRad(270 + 360);
        assertEquals(Math.PI / 2F, rad, 0.001);

        rad = MathUtils.Kinematics.navXToRad(270 - 360);
        assertEquals(Math.PI / 2F, rad, 0.001);
    }

    /**
     * Should be a complete rotation around a circle (dpos = 0)
     */
    @Test
    public void arcDposArcStraight0Heading()
    {
        // l * pi = 1 (circumference)
        // 1/pi = l
        ImmutableVector absoluteDPosCurve = MathUtils.Kinematics.getAbsoluteDPosCurve(1, 1, 123, 1, 0);
        assertEquals(0, absoluteDPosCurve.x, 1);
        assertEquals(0, absoluteDPosCurve.y, 1);
    }

    @Test
    public void arcDposArcStraight45Heading()
    {
        // l * pi = 1 (circumference)
        // 1/pi = l
        ImmutableVector absoluteDPosCurve = MathUtils.Kinematics.getAbsoluteDPosCurve(1, 1, 123, 1, (double) (Math.PI / 4F));
        assertEquals(Math.sqrt(1 / 2F), absoluteDPosCurve.x, 0.001);
        assertEquals(Math.sqrt(1 / 2F), absoluteDPosCurve.y, 0.001);
    }

    @Test
    public void testabsoluteToRelativeCoord()
    {
        ImmutableVector robotPos = new ImmutableVector(4, 4);

        // We will convert this to absolute coords
        ImmutableVector targetPos = robotPos;

        assertEquals(new ImmutableVector(0, 0), MathUtils.LinearAlgebra.absoluteToRelativeCoord(targetPos, robotPos, 0));
        assertEquals(new ImmutableVector(0, 0), MathUtils.LinearAlgebra.absoluteToRelativeCoord(targetPos, robotPos, 35));
        assertEquals(new ImmutableVector(0, 0), MathUtils.LinearAlgebra.absoluteToRelativeCoord(targetPos, robotPos, -180));

        targetPos = new ImmutableVector(5, 5);

        // (1, 1)
        vectorsCloseEnough(new ImmutableVector(1, 1), MathUtils.LinearAlgebra.absoluteToRelativeCoord(targetPos, robotPos, 0));
        vectorsCloseEnough(new ImmutableVector(-1, -1), MathUtils.LinearAlgebra.absoluteToRelativeCoord(targetPos, robotPos, -Math.PI));
        vectorsCloseEnough(new ImmutableVector(ROOT_2, 0), MathUtils.LinearAlgebra.absoluteToRelativeCoord(targetPos, robotPos, Math.PI / 4));
    }

    @Test
    public void testGetSpeedVector()
    {
        ImmutableVector i = new ImmutableVector(1, 0);
        ImmutableVector j = new ImmutableVector(0, 1);

        vectorsCloseEnough(i, MathUtils.Geometry.getVector(1, 0));
        vectorsCloseEnough(j, MathUtils.Geometry.getVector(1, Math.PI / 2));
        vectorsCloseEnough(i.add(j), MathUtils.Geometry.getVector(ROOT_2, Math.PI / 4));

    }

    private void vectorsCloseEnough(ImmutableVector a, ImmutableVector b)
    {
        Assert.assertTrue(MathUtils.epsilonEquals(a, b, 1E-3));
    }

}
