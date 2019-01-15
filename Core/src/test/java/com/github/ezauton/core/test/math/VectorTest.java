package com.github.ezauton.core.test.math;

import com.github.ezauton.core.trajectory.geometry.ImmutableVector;
import com.github.ezauton.core.utils.MathUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class VectorTest
{
    private static final double DELTA = 1E-5;
    private static final ImmutableVector i = new ImmutableVector(1, 0);
    private static final ImmutableVector j = new ImmutableVector(0, 1);
    private static final ImmutableVector origin = new ImmutableVector(0, 0);

    @Test
    public void testVectorConstructors()
    {
        assertNotEquals(new ImmutableVector(1), new ImmutableVector(1, 1));
    }

    @Test
    public void testAccessingComponents()
    {
        ImmutableVector a = new ImmutableVector(123, 155);

        assertEquals(123, a.get(0), DELTA);
        assertEquals(123, a.get(0), DELTA);
        assertEquals(155, a.get(1), DELTA);
        assertEquals(155, a.get(1), DELTA);
    }

    @Test
    public void testOnly2Components()
    {
        ImmutableVector a = new ImmutableVector(123, 155);
        assertThrows(IndexOutOfBoundsException.class, () -> a.get(2));
    }

    @Test
    public void testPerpendicular()
    {
        assertEquals(j.mul(-1), MathUtils.perp(i));
        assertNotEquals(i, MathUtils.perp(i));
        assertEquals(origin, MathUtils.perp(origin));

        assertEquals(new ImmutableVector(0, 0, 1), MathUtils.cross(new ImmutableVector(1, 0, 0), new ImmutableVector(0, 1, 0)));
    }
}
