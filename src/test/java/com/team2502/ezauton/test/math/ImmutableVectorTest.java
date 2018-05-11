package com.team2502.ezauton.test.math;

import com.team2502.ezauton.trajectory.geometry.ImmutableVector;
import org.junit.Assert;
import org.junit.Test;

public class ImmutableVectorTest {

    @Test
    public void testEq()
    {
        Assert.assertEquals(new ImmutableVector(1,1),new ImmutableVector(1,1));
        Assert.assertNotEquals(new ImmutableVector(1,1),new ImmutableVector(2,1));
    }
}
