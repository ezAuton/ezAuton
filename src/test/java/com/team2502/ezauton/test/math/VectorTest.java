package com.team2502.ezauton.test.math;

import org.joml.ImmutableVector;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class VectorTest
{
    private static final double DELTA = 1E-5;
    private static final ImmutableVector i = new ImmutableVector(1, 0);
    private static final ImmutableVector j = new ImmutableVector(0, 1);
    private static final ImmutableVector origin = new ImmutableVector();

    @Test
    public void testVectorConstructors()
    {
        ImmutableVector origin = new ImmutableVector();
        assertEquals(new ImmutableVector(1), new ImmutableVector(1, 1));
        assertEquals(origin, new ImmutableVector(origin));
    }

    @Test
    public void testAccessingComponents()
    {
        ImmutableVector a = new ImmutableVector(123, 155);

        assertEquals(123, a.x(), DELTA);
        assertEquals(123, a.get(0), DELTA);
        assertEquals(155, a.y(), DELTA);
        assertEquals(155, a.get(1), DELTA);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testOnly2Components()
    {
        ImmutableVector a = new ImmutableVector(123, 155);
        a.get(2);
    }

    @Test
    public void testPerpendicular()
    {
        assertEquals(j.mul(-1), i.perpendicular());
        assertNotEquals(i, i.perpendicular());
        assertEquals(origin, origin.perpendicular());
    }
}
