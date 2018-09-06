package com.team2502.ezauton.test.math;

import com.team2502.ezauton.trajectory.geometry.ImmutableVector;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class ImmutableVectorTest
{


    @Test(expected = IllegalArgumentException.class)
    public void testWrongSize()
    {
        new ImmutableVector(1, 1).assertSize(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCollectionWrongSize()
    {
        ImmutableVector oddOneOut = new ImmutableVector(Arrays.asList(1D, 2D, 3D, 4D, 5D));

        List<ImmutableVector> vectors = new ArrayList<>();
        for(int i = 0; i < 5; i++)
        {
            vectors.add(new ImmutableVector(1, 2, 3, 4));
        }
        vectors.add(oddOneOut);

        ImmutableVector.assertSameDim(vectors);
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

    @Test
    public void testDist()
    {
        assertEquals(Math.sqrt(2), new ImmutableVector(1, 1).dist(new ImmutableVector(0, 0)), 1E-5);
        assertEquals(2, new ImmutableVector(1, 1).dist2(new ImmutableVector(0, 0)), 1E-5);
    }

    @Test
    public void testTruncate()
    {
        assertEquals(new ImmutableVector(1, 1, 1), new ImmutableVector(1, 1, 1, 2, 2, 2).truncateElement(2));
    }

    @Test
    public void testAssertSameDim()
    {
        ImmutableVector.assertSameDim(Arrays.asList(new ImmutableVector(1, 1, 1), new ImmutableVector(1, 2, 3), new ImmutableVector(0, 0, 0)));
    }
}
