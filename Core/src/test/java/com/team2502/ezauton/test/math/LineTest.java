package com.team2502.ezauton.test.math;

import com.team2502.ezauton.trajectory.geometry.ImmutableVector;
import com.team2502.ezauton.utils.MathUtils;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class LineTest
{
    private final double DELTA = 1e-5;
    private MathUtils.Geometry.LineR2 horizontal = new MathUtils.Geometry.LineR2(new ImmutableVector(0, 0), new ImmutableVector(1, 0));
    private MathUtils.Geometry.LineR2 vertical = new MathUtils.Geometry.LineR2(new ImmutableVector(0, 0), new ImmutableVector(0, 1));
    private MathUtils.Geometry.LineR2 diag = new MathUtils.Geometry.LineR2(new ImmutableVector(0, 0), new ImmutableVector(1, 1));
    private MathUtils.Geometry.LineR2 otherDiag = new MathUtils.Geometry.LineR2(new ImmutableVector(0, 0), new ImmutableVector(-1, 1));

    @Test
    public void testEvaluateY()
    {
        assertEquals(horizontal.evaluateY(1), 0, DELTA);
        assertEquals(diag.evaluateY(1), 1, DELTA);

        for(int i = 0; i < 20; i++)
        {
            double ax = (Math.random() - 0.5) * 20;
            double ay = (Math.random() - 0.5) * 20;
            ImmutableVector a = new ImmutableVector(ax, ay);

            double bx = (Math.random() - 0.5) * 20;
            double by = (Math.random() - 0.5) * 20;
            ImmutableVector b = new ImmutableVector(bx, by);

            MathUtils.Geometry.LineR2 line = new MathUtils.Geometry.LineR2(a, b);

            assertEquals(line.evaluateY(ax), ay, DELTA);
            assertEquals(line.evaluateY(bx), by, DELTA);
        }

    }

    @Test
    public void testIntersect()
    {

        for(int i = 0; i < 20; i++)
        {
            double ax = (Math.random() - 0.5) * 20;
            double ay = (Math.random() - 0.5) * 20;
            ImmutableVector a = new ImmutableVector(ax, ay);

            double bx = (Math.random() - 0.5) * 20;
            double by = (Math.random() - 0.5) * 20;
            ImmutableVector b = new ImmutableVector(bx, by);

            double cx = (Math.random() - 0.5) * 20;
            double cy = (Math.random() - 0.5) * 20;
            ImmutableVector c = new ImmutableVector(cx, cy);

            MathUtils.Geometry.LineR2 lineAB = new MathUtils.Geometry.LineR2(a, b);
            MathUtils.Geometry.LineR2 lineBC = new MathUtils.Geometry.LineR2(b, c);

            assertEquals(b, lineAB.intersection(lineBC));
            assertEquals(b, lineBC.intersection(lineAB));
        }

        Assert.assertNull(horizontal.intersection(horizontal));
    }

    @Test
    public void testLineEquals()
    {
        String notALine = "";
        assertNotEquals(horizontal, notALine);
        assertEquals(horizontal, horizontal);

        assertNotEquals(horizontal, vertical);

    }

    //    @Test //TODO: fix
    public void testPerp()
    {
        assertEquals(otherDiag, diag.getPerp(new ImmutableVector(0, 0)));
        assertEquals(diag, otherDiag.getPerp(new ImmutableVector(0, 0)));

        assertEquals(horizontal, vertical.getPerp(new ImmutableVector(0, 0)));
        assertEquals(vertical,
                     horizontal.getPerp(new ImmutableVector(0, 0))
                    );
    }

    @Test
    public void testIntegrate()
    {
        assertEquals(0.5, diag.integrate(0, 1), DELTA);
        assertEquals(0.5, diag.integrate(), DELTA);
        assertEquals(0, diag.integrate(-1, 1), DELTA);
    }
}
