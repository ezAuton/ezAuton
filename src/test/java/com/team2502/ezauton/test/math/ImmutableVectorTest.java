package com.team2502.ezauton.test.math;

import com.team2502.ezauton.trajectory.geometry.ImmutableVector;
import org.junit.Test;

import static org.junit.Assert.*;

public class ImmutableVectorTest
{


    @Test(expected = IllegalArgumentException.class)
    public void testWrongSize()
    {
        new ImmutableVector(1, 1).assertSize(1);
    }

    @Test
    public void testEq()
    {
        assertEquals(new ImmutableVector(1, 1), new ImmutableVector(1, 1));
        assertNotEquals(new ImmutableVector(1, 1), new ImmutableVector(2, 1));
    }

    @Test
    public void testOf()
    {
        assertEquals(new ImmutableVector(4, 4, 4), ImmutableVector.of(4, 3));
    }

    @Test
    public void testMultiply()
    {
        assertEquals(new ImmutableVector(3, 6, 9), new ImmutableVector(1, 2, 3).mul(3));
    }

    @Test
    public void testDot()
    {
        assertEquals(0, new ImmutableVector(1, 0, 0).dot(new ImmutableVector(0, 1, 0)), 1E-6);
    }

    @Test
    public void testMag()
    {
        assertEquals(Math.sqrt(27), ImmutableVector.of(3, 3).mag(), 1E-6);
    }

    @Test
    public void testHashCode()
    {
        assertTrue(ImmutableVector.of(3, 4).hashCode() == ImmutableVector.of(3, 4).hashCode());
    }
}
