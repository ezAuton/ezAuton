import com.team2502.ezauton.utils.MathUtils;
import org.joml.ImmutableVector;
import org.junit.Assert;
import org.junit.Test;

public class LineTest
{
    private MathUtils.Geometry.Line horizontal = new MathUtils.Geometry.Line(new ImmutableVector(0, 0), new ImmutableVector(1, 0));
    private MathUtils.Geometry.Line vertical = new MathUtils.Geometry.Line(new ImmutableVector(0, 0), new ImmutableVector(0, 1));
    private MathUtils.Geometry.Line diag = new MathUtils.Geometry.Line(new ImmutableVector(0, 0), new ImmutableVector(1, 1));

    private final double DELTA = 1e-5;

    @Test
    public void testEvaluateY()
    {
        Assert.assertEquals(horizontal.evaluateY(1), 0, DELTA);
        Assert.assertEquals(diag.evaluateY(1), 1, DELTA);

        for(int i = 0; i < 20; i++)
        {
            double ax = (Math.random() - 0.5) * 20;
            double ay = (Math.random() - 0.5) * 20;
            ImmutableVector a = new ImmutableVector(ax, ay);

            double bx = (Math.random() - 0.5) * 20;
            double by = (Math.random() - 0.5) * 20;
            ImmutableVector b = new ImmutableVector(bx, by);

            MathUtils.Geometry.Line line = new MathUtils.Geometry.Line(a, b);

            Assert.assertEquals(line.evaluateY(ax), ay, DELTA);
            Assert.assertEquals(line.evaluateY(bx), by, DELTA);
        }

    }

    @Test
    public void testIntersect()
    {
        Assert.assertEquals(horizontal.evaluateY(1), 0, DELTA);
        Assert.assertEquals(diag.evaluateY(1), 1, DELTA);

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

            Assert.assertEquals(b, lineAB.intersection(lineBC));
            Assert.assertEquals(b, lineBC.intersection(lineAB));
        }
    }


}
