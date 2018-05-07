import com.team2502.ezauton.utils.MathUtils;
import org.joml.ImmutableVector;
import org.junit.Assert;
import org.junit.Test;

public class MathTest
{
    private ImmutableVector e1 = new ImmutableVector(1, 0);

    private final double DELTA = 1E-5;
    @Test
    public void testRotation90()
    {
        ImmutableVector rotated90 = MathUtils.LinearAlgebra.rotate2D(e1, MathUtils.PI_F / 2);

        Assert.assertEquals(0,rotated90.x, 0.001);
        Assert.assertEquals(1,rotated90.y, 0.001);
    }

    @Test
    public void testRotation720()
    {
        ImmutableVector rotated720 = MathUtils.LinearAlgebra.rotate2D(e1, MathUtils.PI_F*2);

        Assert.assertEquals(1,rotated720.x, 0.001);
        Assert.assertEquals(0,rotated720.y, 0.001);
    }

    @Test
    public void testPosRotationCoordinateTransform()
    {
        ImmutableVector robotLocation = new ImmutableVector(1,1);
        double robotHeading = 7F*MathUtils.PI_F/4;
        ImmutableVector absoluteCoord = new ImmutableVector(2,2);

        double distance = robotLocation.distance(absoluteCoord);

        ImmutableVector relativeCoord = MathUtils.LinearAlgebra.absoluteToRelativeCoord(absoluteCoord, robotLocation, robotHeading);

        Assert.assertEquals(0,relativeCoord.x, 0.001);
        Assert.assertEquals(distance,relativeCoord.y, 0.001);
    }

    @Test
    public void testNegRotationCoordinateTransform()
    {
        ImmutableVector robotLocation = new ImmutableVector(1,1);
        double robotHeading = -MathUtils.PI_F/4;
        ImmutableVector absoluteCoord = new ImmutableVector(2,2);

        double distance = robotLocation.distance(absoluteCoord);

        ImmutableVector relativeCoord = MathUtils.LinearAlgebra.absoluteToRelativeCoord(absoluteCoord, robotLocation, robotHeading);

        Assert.assertEquals(0,relativeCoord.x, 0.001);
        Assert.assertEquals(distance,relativeCoord.y, 0.001);
    }

    @Test //fail
    public void testAbsoluteDPos45()
    {
        ImmutableVector dPos = MathUtils.Kinematics.getAbsoluteDPosLine(1, 1, 1F, (double) (Math.PI / 4F));

        Assert.assertEquals(Math.sqrt(1/2F),dPos.x, 0.001);
        Assert.assertEquals(Math.sqrt(1/2F),dPos.y, 0.001);
    }

    @Test
    public void navXToRad()
    {
        // TODO: returns cw radians not ccw I think
        double rad = MathUtils.Kinematics.navXToRad(270);

        Assert.assertEquals(Math.PI/2F,rad, 0.001);

        rad = MathUtils.Kinematics.navXToRad(270+360);
        Assert.assertEquals(Math.PI/2F,rad, 0.001);

        rad = MathUtils.Kinematics.navXToRad(270-360);
        Assert.assertEquals(Math.PI/2F,rad, 0.001);
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
        Assert.assertEquals(0,absoluteDPosCurve.x, 1);
        Assert.assertEquals(0,absoluteDPosCurve.y, 1);
    }

    @Test
    public void arcDposArcStraight45Heading()
    {
        // l * pi = 1 (circumference)
        // 1/pi = l
        ImmutableVector absoluteDPosCurve = MathUtils.Kinematics.getAbsoluteDPosCurve(1, 1, 123, 1, (double) (Math.PI / 4F));
        Assert.assertEquals(Math.sqrt(1/2F),absoluteDPosCurve.x, 0.001);
        Assert.assertEquals(Math.sqrt(1/2F),absoluteDPosCurve.y, 0.001);
    }

    /**
     * Should be a complete rotation around a circle (dpos = 0)
     */
    @Test
    public void testDTheta90()
    {
        double dTheta = MathUtils.Geometry.getDThetaNavX(270, 0);
        Assert.assertEquals(3F*Math.PI/2F, dTheta,0.001);
    }

    /**
     * Should be a complete rotation around a circle (dpos = 0)
     */
    @Test
    public void testCCWClosest90()
    {
        Assert.assertFalse(MathUtils.Geometry.isCCWQuickest(0, 90));
    }

    /**
     * Should be a complete rotation around a circle (dpos = 0)
     */
    @Test
    public void testCCWClosest90Opp()
    {
        Assert.assertTrue(MathUtils.Geometry.isCCWQuickest(90, 0));
    }

    /**
     * Should be a complete rotation around a circle (dpos = 0)
     */
    @Test
    public void testCCWClosestStrangeAngles()
    {
        Assert.assertTrue(MathUtils.Geometry.isCCWQuickest(127, 359));
    }

    @Test
    public void testNavXBound()
    {
        Assert.assertEquals(355,MathUtils.Kinematics.navXBound(-5) , 0.001);
    }

    @Test
    public void testSignSame()
    {
        Assert.assertTrue(MathUtils.signSame(Double.MAX_VALUE, Double.MAX_VALUE));
        Assert.assertTrue(MathUtils.signSame(1234.4, 1234.1));
        Assert.assertTrue(MathUtils.signSame(1234.4, 1234.4));
        Assert.assertTrue(MathUtils.signSame(1/3.0, 1/3.0));
        Assert.assertTrue(MathUtils.signSame(0, 0));
        Assert.assertFalse(MathUtils.signSame(-1234.4, 1234.1));
        Assert.assertFalse(MathUtils.signSame(-1234.4, 1234.4));
        Assert.assertFalse(MathUtils.signSame(-1/3.0, 1/3.0));
    }

    @Test
    public void testMinAbs()
    {
        
        Assert.assertEquals(MathUtils.minAbs(3, 5), 3, DELTA);
        Assert.assertEquals(MathUtils.minAbs(3, -5), 3, DELTA);
        Assert.assertEquals(MathUtils.minAbs(-3, -5), -3, DELTA);
        Assert.assertEquals(MathUtils.minAbs(-3, -5/3.0), -5/3.0, DELTA);
        Assert.assertEquals(MathUtils.minAbs(-3, 3), -3, DELTA);
        Assert.assertEquals(MathUtils.minAbs(3, -3), 3, DELTA);
    }

    @Test
    public void testMaxAbs()
    {
        
        Assert.assertEquals(MathUtils.maxAbs(3, 5), 5, DELTA);
        Assert.assertEquals(MathUtils.maxAbs(3, -5), -5, DELTA);
        Assert.assertEquals(MathUtils.maxAbs(-3, -5), -5, DELTA);
        Assert.assertEquals(MathUtils.maxAbs(-3, -5/3.0), -3, DELTA);
        Assert.assertEquals(MathUtils.maxAbs(-3, 3), -3, DELTA);
        Assert.assertEquals(MathUtils.maxAbs(3, -3), 3, DELTA);
    }

    @Test
    public void testDegToRad()
    {
        for(int i = 0; i < 20; i++)
        {
            double deg = Math.random() * 360;
            double rad = MathUtils.deg2Rad(deg);

            Assert.assertEquals(rad, Math.toRadians(deg), DELTA);
        }
    }

    @Test
    public void testRadToGegree()
    {
        for(int i = 0; i < 20; i++)
        {
            double rad = Math.random() * 360;
            double deg = MathUtils.rad2Deg(rad);

            Assert.assertEquals(deg, Math.toDegrees(rad), DELTA);
        }
    }

    @Test
    public void testEpsilonEqualsNumbers()
    {
        for(int i = 0; i < 20; i++)
        {
            double a = Math.random() * 360;
            double b = (Math.sqrt(a * a) * 3.0) / 3 + 1.987 - 1 - 0.987; // try to accumulate FP errors

            Assert.assertTrue(MathUtils.epsilonEquals(a, b));
            Assert.assertTrue(MathUtils.epsilonEquals((float) a, (float) b));
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

            Assert.assertEquals(vecA, vecB);
            Assert.assertTrue(MathUtils.epsilonEquals(vecA, vecB));
        }
    }

    @Test
    public void testFloor()
    {
        for(int i = 0; i < 20; i++)
        {
            double a = (Math.random() - 0.5) * 2 *  360;
            Assert.assertEquals(MathUtils.floor(a), Math.floor(a), DELTA);
            Assert.assertEquals(MathUtils.lfloor(a), Math.floor(a), DELTA);
            Assert.assertEquals(MathUtils.floor((float) a), Math.floor((float) a), DELTA);
        }
    }

}
