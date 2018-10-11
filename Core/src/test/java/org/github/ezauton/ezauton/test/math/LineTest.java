package org.github.ezauton.ezauton.test.math;

import org.github.ezauton.ezauton.trajectory.geometry.ImmutableVector;
import org.github.ezauton.ezauton.utils.MathUtils;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class LineTest
{
    private final double DELTA = 1e-5;
    private MathUtils.Geometry.Line horizontal = new MathUtils.Geometry.Line(new ImmutableVector(0, 0), new ImmutableVector(1, 0));
    private MathUtils.Geometry.Line vertical = new MathUtils.Geometry.Line(new ImmutableVector(0, 0), new ImmutableVector(0, 1));
    private MathUtils.Geometry.Line diag = new MathUtils.Geometry.Line(new ImmutableVector(0, 0), new ImmutableVector(1, 1));
    private MathUtils.Geometry.Line otherDiag = new MathUtils.Geometry.Line(new ImmutableVector(0, 0), new ImmutableVector(-1, 1));

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

            MathUtils.Geometry.Line line = new MathUtils.Geometry.Line(a, b);

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

            MathUtils.Geometry.Line lineAB = new MathUtils.Geometry.Line(a, b);
            MathUtils.Geometry.Line lineBC = new MathUtils.Geometry.Line(b, c);

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
