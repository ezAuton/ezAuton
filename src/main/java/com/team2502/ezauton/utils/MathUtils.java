package com.team2502.ezauton.utils;

import org.joml.ImmutableVector;

import java.util.HashSet;
import java.util.Set;

/**
 * A class containing very many useful mathematical constants and operations
 *
 * @see MathUtils.Algebra
 * @see MathUtils.Geometry
 * @see MathUtils.LinearAlgebra
 * @see MathUtils.Kinematics
 */
//@SuppressWarnings("unused")
public final class MathUtils
{
    public static final double PHI = 1.618033989D;
    public static final double ROOT_2 = 1.414213562D;
    public static final double ROOT_3 = 1.732050808D;

    public static final double TAU = 2 * Math.PI;

    public static final ImmutableVector VECTOR_STRAIGHT = new ImmutableVector(1, 0);

    private static final double ln2 = StrictMath.log(2);
    private static final double ln3 = StrictMath.log(3);
    private static final double ln4 = StrictMath.log(4);
    private static final double ln5 = StrictMath.log(5);
    private static final double ln6 = StrictMath.log(6);
    private static final double ln7 = StrictMath.log(7);
    private static final double ln8 = StrictMath.log(8);
    private static final double ln9 = StrictMath.log(9);

    /**
     * A table of sin values computed from 0 (inclusive) to 2π (exclusive), with steps of 2π / 65536.
     * <br>
     * Because this stores 2^16 numbers, we will let it be filled with floats.
     * After all, java allows for implicit float->double casting
     */
    private static final double[] SIN_TABLE = new double[65536];

    private MathUtils()
    {

    }

    static {
        for(int i = 0; i < 65536; ++i) { SIN_TABLE[i] = Math.sin((i) * Math.PI * 2.0D / 65536.0D); }

        SIN_TABLE[0] = 0;       /* 0π */
        SIN_TABLE[16384] = 1;   /* π/2 */
        SIN_TABLE[32768] = 0;   /* 2π */
        SIN_TABLE[49152] = -1;  /* 3π/2 */
    }

    public static void init()
    {}

    public static double shiftRadiansBounded(double initRadians, double shift)
    {
        return (initRadians + shift) % TAU;
    }

    /**
     * @param x A number
     * @param y Another number
     * @return Returns true if numbers are same sign
     */
    public static boolean signSame(double x, double y)
    { return Long.signum(Double.doubleToLongBits(x)) == Long.signum(Double.doubleToLongBits(y)); }

    /**
     * sin looked up in a table
     */
    public static double sin(final double value)
    { return SIN_TABLE[(int) (value * 10430.378D) & 65535]; }

    /**
     * @param a a number
     * @param b another number
     * @return the number closer to 0, or the {@code a} if |a| = |b|
     */
    public static double minAbs(final double a, final double b)
    {
        return Math.abs(a) > Math.abs(b) ? b : a;
    }

    /**
     * @param a a number
     * @param b another number
     * @return the number farther from 0, or {@code a} if |a| = |b|
     */
    public static double maxAbs(final double a, final double b)
    {
        return Math.abs(a) < Math.abs(b) ? b : a;
    }

    /**
     * cos looked up in the sin table with the appropriate offset
     */
    public static double cos(final double value)
    { return SIN_TABLE[(int) (value * 10430.378F + 16384.0F) & 65535]; }

    /**
     * @param a Lower/Upper bound
     * @param x Vector to check
     * @param c Upper/Lower bound
     * @return Returns true if x's x-component is in between that of a and c AND if x's y component is in between that of a and c.
     * @see MathUtils.Algebra#between(double, double, double)
     */
    public static boolean between(final ImmutableVector a, final ImmutableVector x, final ImmutableVector c)
    {
        return Algebra.between(a.get(0), x.get(0), c.get(0)) && Algebra.between(a.get(1), x.get(1), c.get(1));
    }

    public static double min(double... nums)
    {
        double min = nums[0];
        for(double num : nums)
        {
            min = Math.min(num, min);
        }
        return min;
    }
    /**
     * Multiply degrees by π/180
     *
     * @param deg Number of degrees
     * @return Number of radians
     */
    public static double deg2Rad(double deg)
    { return deg * 0.01745329251994D; }

    /**
     * Multiply degrees by 180/π
     *
     * @param rad Number of radians
     * @return Number of degrees
     */
    public static double rad2Deg(double rad)
    { return rad * 57.29577951308233D; }


    public static boolean epsilonEquals(ImmutableVector vecA, ImmutableVector vecB)
    { return epsilonEquals(vecA.x, vecB.x) && epsilonEquals(vecA.y, vecB.y); }

    public static boolean epsilonEquals(ImmutableVector vecA, ImmutableVector vecB, final double delta)
    { return epsilonEquals(vecA.x, vecB.x, delta) && epsilonEquals(vecA.y, vecB.y, delta); }

    /**
     * Checks if two numbers are equal while accounting for
     * the possibility of a floating point error.
     *
     * @return x ~= y
     */
    public static boolean epsilonEquals(final double x, final double y)
    { return Math.abs(y - x) < 1.0E-5D; }

    /**
     * Checks if two numbers are equal while accounting for
     * the possibility of a floating point error.
     *
     * @return x ~= y
     */
    public static boolean epsilonEquals(final double x, final double y, final double delta)
    { return Math.abs(y - x) < delta; }


    /**
     * Returns the greatest integer less than or equal to the double argument
     */
    public static int floor(final double value)
    {
        int i = (int) value;
        return value < (double) i ? i - 1 : i;
    }

    /**
     * Long version of floor()
     */
    public static long lfloor(final double value)
    {
        long i = (long) value;
        return value < (double) i ? i - 1L : i;
    }

    /**
     * Gets the decimal portion of the given double. For instance, {@code frac(5.5)} returns {@code .5}.
     */
    public static double decimalComponent(final double number)
    { return number - floor(number); }


    //region Logarithmic Functions
    /**
     * Allows for the calculate of logX(in), may have minor performance boost from using direct call to StrictMath lowering stack overhead.
     *
     * @param base The base of the logPop.
     * @param in   The value to find the logPop of.
     * @return The logX(in)
     */
    public static double log(final double base, final double in)
    { return StrictMath.log(in) / StrictMath.log(base); }

    /**
     * Use the predefined square logPop instead of a custom implementation.
     *
     * @param in The value to find the logPop of.
     * @return The log2(in)
     */
    public static double log2(final double in)
    { return StrictMath.log(in) / ln2; }

    /**
     * Use the predefined cube logPop instead of a custom implementation.
     *
     * @param in The value to find the logPop of.
     * @return The log3(in)
     */
    public static double log3(final double in)
    { return StrictMath.log(in) / ln3; }

    /**
     * Use pre calculated math for optimization.
     *
     * @param in The value to find the logPop of.
     * @return The log4(in)
     */
    public static double log4(final double in)
    { return StrictMath.log(in) / ln4; }

    /**
     * Use pre calculated math for optimization.
     *
     * @param in The value to find the logPop of.
     * @return The log5(in)
     */
    public static double log5(final double in)
    { return StrictMath.log(in) / ln5; }

    /**
     * Use pre calculated math for optimization.
     *
     * @param in The value to find the logPop of.
     * @return The log6(in)
     */
    public static double log6(final double in)
    { return StrictMath.log(in) / ln6; }

    /**
     * Use pre calculated math for optimization.
     *
     * @param in The value to find the logPop of.
     * @return The log7(in)
     */
    public static double log7(final double in)
    { return StrictMath.log(in) / ln7; }

    /**
     * Use pre calculated math for optimization.
     *
     * @param in The value to find the logPop of.
     * @return The log8(in)
     */
    public static double log8(final double in)
    { return StrictMath.log(in) / ln8; }

    /**
     * Use pre calculated math for optimization.
     *
     * @param in The value to find the logPop of.
     * @return The log9(in)
     */
    public static double log9(final double in)
    { return StrictMath.log(in) / ln9; }

    /**
     * Use pre calculated math for optimization.
     *
     * @param in The value to find the logPop of.
     * @return The log10(in)
     */
    public static double log10(final double in)
    { return StrictMath.log10(in); }


    /**
     * Calculates the natural logarithm (base e).
     *
     * @param in The value to find the logPop of.
     * @return The ln(in)
     */
    public static double ln(final double in)
    { return StrictMath.log(in); }

    //region Exponentiation Functions

    /**
     * @return x ^ 2
     */
    public static double pow2(final double x)
    { return x * x; }

    /**
     * @return x ^ 3
     */
    public static double pow3(final double x)
    { return x * x * x; }

    /**
     * @return x ^ 4
     */
    public static double pow4(final double x)
    { return x * x * x * x; }

    /**
     * @return x ^ 5
     */
    public static double pow5(final double x)
    { return x * x * x * x * x; }

    /**
     * @return x ^ 6
     */
    public static double pow6(final double x)
    { return x * x * x * x * x * x; }

    /**
     * @return x ^ 7
     */
    public static double pow7(final double x)
    { return x * x * x * x * x * x * x; }

    /**
     * @return x ^ 8
     */
    public static double pow8(final double x)
    { return x * x * x * x * x * x * x * x; }

    /**
     * @return x ^ 9
     */
    public static double pow9(final double x)
    { return x * x * x * x * x * x * x * x * x; }

    /**
     * @return x ^ 10
     */
    public static double pow10(final double x)
    { return x * x * x * x * x * x * x * x * x * x; }
    //endregion

    public interface Integrable
    {
        double integrate(double a, double b);
    }

    @FunctionalInterface
    public interface Function
    {
        double get(double x);
    }

    /**
     * A class containing methods pertaining to vector manipulation
     */
    public static class LinearAlgebra
    {
        /**
         * Rotate the input vector theta radians counterclockwise
         *
         * @param vector The input vecto
         * @param theta  How much to rotate it by (radians)
         * @return The rotated vector
         */
        public static ImmutableVector rotate2D(ImmutableVector vector, double theta)
        {
            double sin = sin(theta);
            double cos = cos(theta);
            ImmutableVector immutableVector = new ImmutableVector((vector.get(0) * cos - vector.get(1) * sin),
                                                                  (vector.get(0) * sin + vector.get(1) * cos));
            return immutableVector;
        }

        /**
         * Turn absolute coordinates into coordinates relative to the robot
         *
         * @param coordinateAbsolute The absolute coordinates
         * @param robotCoordAbs The robot's absolute position
         * @param robotHeading The robot's heading (radians)
         * @return {@code cordinateAbsolute} but relative to the robot
         */
        public static ImmutableVector absoluteToRelativeCoord(ImmutableVector coordinateAbsolute, ImmutableVector robotCoordAbs, double robotHeading)
        { return rotate2D(coordinateAbsolute.sub(robotCoordAbs), -robotHeading); }
    }
    /**
     * A class containing methods pertaining to manipulation of real numbers
     */
    public static class Algebra
    {

        /**
         * Solve for the roots of a quadratic of the form ax^2 + bx + c
         *
         * @param a x^2 coefficient
         * @param b x coefficient
         * @param c added constant
         * @return roots of the quadratic
         */
        public static Set<Double> quadratic(double a, double b, double c)
        {
            Set<Double> toReturn = new HashSet<>();
            double discriminate = discriminate(a, b, c);
            if(discriminate < 0)
            {
                return toReturn;
            }
            else if(discriminate == 0)
            {
                toReturn.add(-b / (2 * a));
            }
            else
            {
                double LHS = -b / (2 * a);
                double RHS = (Math.sqrt(discriminate) / (2 * a));
                toReturn.add(LHS + RHS);
                toReturn.add(LHS - RHS);
            }
            return toReturn;
        }

        /**
         * Solve for the discriminant of a quadratic of the form ax^2 + bx + c
         *
         * @param a x^2 coefficient
         * @param b x coefficient
         * @param c added thing
         * @return roots of the quadratic
         * @see MathUtils.Algebra#quadratic
         */
        public static double discriminate(double a, double b, double c)
        {
            return b * b - 4 * a * c;
        }

        /**
         * @return if a <= x <= b or b<= x <= a
         */
        public static boolean between(final double a, final double x, final double b)
        {
            return bounded(a, x, b) || bounded(b, x, a);
        }


        /**
         * @param a lower bound
         * @param x some number
         * @param b upper bound
         * @return if x is between lower and upper bound
         */
        public static boolean bounded(final double a, final double x, final double b)
        {
            return a <= x && x <= b;
        }

    }

    /**
     * A class containing methods pertaining to determining our robot's movement information
     */
    public static class Kinematics
    {

        /**
         * Get the 1D position of the robot given p0, v0, a0, and dt. Uses elementary physics formulas.
         *
         * @param posInit
         * @param velocityInit
         * @param accelerationInit
         * @param dt
         * @return
         */
        public static double getPos(double posInit, double velocityInit, double accelerationInit, double dt)
        {
            return posInit + velocityInit * dt + 1 / 2F * accelerationInit * dt * dt;
        }

        /**
         *
         * @param leftVel
         * @param rightVel
         * @param lateralWheelDistance
         * @return positive CCW, negative CW
         */
        public static double getAngularVel(double leftVel, double rightVel, double lateralWheelDistance)
        {
            return (rightVel - leftVel) / lateralWheelDistance;
        }

        /**
         * @param vL
         * @param vR
         * @param l
         * @return The radius of the circle traveling across .. positive if CCW
         */
        public static double getTrajectoryRadius(double vL, double vR, double l)
        {
            return (l * (vR + vL)) / (2 * (vR - vL));
        }

        /**
         * The relative difference in position using arcs
         * @param vL
         * @param vR
         * @param l
         * @param dt
         * @return
         */
        public static ImmutableVector getRelativeDPosCurve(double vL, double vR, double l, double dt)
        {
            // To account for an infinite pathplanning radius when going straight
            if(Math.abs(vL - vR) <= (vL + vR) * 1E-2)
            {
                // Probably average is not needed, but it may be useful over long distances
                return new ImmutableVector(0, (vL + vR) / 2F * dt);
            }
            double w = getAngularVel(vL, vR, l);
            double dTheta = w * dt;

            double r = getTrajectoryRadius(vL, vR, l);

            double dxRelative = -r * (1 - MathUtils.cos(-dTheta));
            double dyRelative = -r * MathUtils.sin(-dTheta);

            return new ImmutableVector(dxRelative, dyRelative);
        }

        public static double getTangentialSpeed(double wheelL, double wheelR)
        {
            return (wheelL + wheelR) / 2F;
        }


        public static ImmutableVector getAbsoluteDPosLine(double vL, double vR, double dt, double robotHeading)
        {
            double tangentialSpeed = getTangentialSpeed(vL, vR);
            double tangentialDPos = getTangentialSpeed(vL, vR) * dt;
            ImmutableVector dPos = VECTOR_STRAIGHT.mul(tangentialDPos);
            return LinearAlgebra.rotate2D(dPos, robotHeading);
        }

        public static ImmutableVector getAbsoluteDPosCurve(double vL, double vR, double l, double dt, double robotHeading)
        {
            ImmutableVector relativeDPos = getRelativeDPosCurve(vL, vR, l, dt);
            return LinearAlgebra.rotate2D(relativeDPos, robotHeading);
        }

        /**
         * Turn NavX angle into radians (navX 0 degrees is facing (0,1) ... increases CW)
         *
         * @param yawDegTot What the NavX is reading
         * @return The angle in radians, between 0 and 2pi.
         */

        public static double navXToRad(double yawDegTot)
        {
            double yawDeg = -yawDegTot % 360;
            if(yawDeg < 0) { yawDeg = 360 + yawDeg; }
            return MathUtils.deg2Rad(yawDeg);
        }

        /**
         * turn an angle without bounds (-inf,inf) to [0,360)
         *
         * @param angle Whatever the navX is reading
         * @return An angle between 0 and 360, in degrees
         */
        public static double navXBound(double angle)
        {
            double bounded = angle % 360;
            if(bounded < 0)
            {
                return 360 + bounded;
            }
            return bounded;
        }
    }


    public static class Geometry
    {
        /**
         * @param initDegrees  init degrees navX (clockwise)
         * @param finalDegrees final degrees navX (counterclockwise)
         * @return the difference in radians between the two degrees from [0,2pi). Increases counterclockwise.
         */
        public static double getDThetaNavX(double initDegrees, double finalDegrees)
        {
            double degDif = -(finalDegrees - initDegrees);
            double radians = MathUtils.deg2Rad(degDif);
            double radBounded = (radians % TAU);
            if(radBounded < 0) { return (TAU + radBounded); }
            return radBounded;
        }

        /**
         * Return the difference between 2 angles (in degrees)
         * @param angle1 Angle 1 (in degrees)
         * @param angle2 Angle 2 (in degrees)
         * @return Difference between the angles (in degrees)
         */
        public static double getDAngle(double angle1, double angle2)
        {
            double simpleAngle1 = angle1 % 360;
            double simpleAngle2 = angle2 % 360;
            double dif = Math.abs(simpleAngle1 - simpleAngle2);
            if(dif > 180)
            {
                dif = 360 - dif;
            }
            return dif;
        }

        public static boolean isCCWQuickest(double angleInit, double angleFinal)
        {
            double d;
            if(angleFinal > angleInit)
            {
                d = angleFinal - angleInit;
                if(d > 180)
                {
                    // Since angles are by default cw (navX) this means we should go ccw
                    return true;
//                    d = 360-d;
                }
            }
            else if(angleInit > angleFinal)
            {
                d = angleInit - angleFinal;
                if(d > 180)
                {
                    d = 360 - d;
                }
                else
                {
                    return true;
//                    ccw = true;
                }
            }
            return false;
        }

        /**
         * @param start The starting vector
         * @param end The ending vector
         * @return The theta of the angle created counterclockwise between \vec{i} and the line from start->end
         * <br>
         * Equivalent to arctan((start - end))
         */
        public static double getThetaFromPoints(ImmutableVector start, ImmutableVector end)
        {
            double dx = end.x - start.x;
            double dy = end.y - start.y;
            return Math.atan2(dy, dx);
        }

        /**
         * Given a line defined by two points, find the point on the line closest to our robot's position
         *
         * @param linePointA One point on the line
         * @param linePointB Another point on the line
         * @param robotPos   The point at which our robot is
         * @return The point <b>on the line segment</b> closest to the robot
         */
        public static ImmutableVector getClosestPointLineSegments(ImmutableVector linePointA, ImmutableVector linePointB, ImmutableVector robotPos)
        {

            double distToA = Math.hypot(linePointA.x - robotPos.x, linePointA.y - robotPos.y);
            double distToB = Math.hypot(linePointB.x - robotPos.x, linePointB.y - robotPos.y);

            Line lineSegment = new Line(linePointA, linePointB);

            Line linePerp = lineSegment.getPerp(robotPos);

            ImmutableVector intersect = linePerp.intersection(lineSegment);



            double distToIntersect = Math.hypot(intersect.x - robotPos.x, intersect.y - robotPos.y);

            if(!between(linePointA, intersect, linePointB))
            {
                distToIntersect = Double.MAX_VALUE; // This way, we will not select the line intersect as the closest point on the line

            }

            double minDist = min(distToA, distToB, distToIntersect);
            if(minDist == distToA)
            {
                return linePointA;
            }
            else if(minDist == distToB)
            {
                return linePointB;
            }
            else
            {
                return intersect;
            }
        }

        /**
         * @param speed Vector's magnitude
         * @param angle Angle at which it is at
         * @return A vector in <x, y> form
         * @see ImmutableVector
         */
        public static ImmutableVector getVector(double speed, double angle)
        {
            return MathUtils.LinearAlgebra.rotate2D(VECTOR_STRAIGHT, angle).mul(speed);
        }

        /**
         * Given a circle and a line, find where the circle intersects the line
         *
         * @param pointA One point on the line
         * @param pointB Another point on the line
         * @param center The center of the circle
         * @param radius The radius of the circle
         * @return All points on both the line and circle, should they exist.
         */
        public static ImmutableVector[] getCircleLineIntersectionPoint(ImmutableVector pointA, ImmutableVector pointB, ImmutableVector center, double radius)
        {
            double baX = pointB.get(0) - pointA.get(0);
            double baY = pointB.get(1) - pointA.get(1);

            double caX = center.get(0) - pointA.get(0);
            double caY = center.get(1) - pointA.get(1);

            double a = baX * baX + baY * baY;
            double bBy2 = baX * caX + baY * caY;
            double c = caX * caX + caY * caY - radius * radius;

            double pBy2 = bBy2 / a;
            double q = c / a;

            double disc = pBy2 * pBy2 - q;
            if(disc < 0) { return new ImmutableVector[0]; }
            // if disc == 0 ... dealt with later
            double tmpSqrt = Math.sqrt(disc);
            double abScalingFactor1 = tmpSqrt - pBy2;

            ImmutableVector p1 = new ImmutableVector(pointA.get(0) - baX * abScalingFactor1, pointA.get(1) - baY * abScalingFactor1);
            if(disc == 0) { return new ImmutableVector[] { p1 }; }

            double abScalingFactor2 = -pBy2 - tmpSqrt;
            ImmutableVector p2 = new ImmutableVector(pointA.get(0) - baX * abScalingFactor2, pointA.get(1) - baY * abScalingFactor2);
            return new ImmutableVector[] { p1, p2 };
        }

        public static ImmutableVector[] getCircleLineIntersectionPoint(Line line, ImmutableVector center, double radius)
        {
            return getCircleLineIntersectionPoint(line.a, line.b, center, radius);
        }

        public static class Line implements Integrable
        {
            final double slope;
            final double y_intercept;
            final double x_intercept;

            final double x1;
            final double x2;
            final double y1;
            final double y2;

            final ImmutableVector a;
            final ImmutableVector b;

            public Line(ImmutableVector a, ImmutableVector b)
            {
                x1 = a.x;
                x2 = b.x;
                y1 = a.y;
                y2 = b.y;

                this.a = a;
                this.b = b;

                if(a.x - b.x != 0)
                {
                    slope = (a.y - b.y) / (a.x - b.x);

                }
                else
                {
                    slope = Double.MAX_VALUE;
                }
                y_intercept = a.y - slope * a.x;
                x_intercept = -y_intercept / slope;
            }


            public double evaluateY(double x)
            {
                return slope * x + y_intercept;
            }

            public double integrate(double a, double b)
            {
                // integral of y = mx + b is
                // mx^2/2 + bx + c
                // at start of integration bound it should be 0
                double c = -(a * a / 2 + b * a);

                Function indefiniteIntegral = (x) -> slope * x * x / 2 + y_intercept * x + c;

                return indefiniteIntegral.get(b) - indefiniteIntegral.get(a);
            }

            public double integrate()
            {
                return integrate(x1, x2);
            }

            public Line getPerp(ImmutableVector point)
            {
                double perpSlope;
                if(slope == Double.MAX_VALUE || slope == Double.POSITIVE_INFINITY)
                {
                    perpSlope = 0;
                }
                else if(slope == 0)
                {
                    perpSlope = Double.MAX_VALUE;
                }
                else
                {
                    perpSlope = -1 / slope;
                }
                return new Line(point, new ImmutableVector(point.x + 1, (point.y + perpSlope)));
            }

            public ImmutableVector intersection(Line other)
            {
                if(other.slope == this.slope)
                {
                    return null; // lines do not intersect
                }
                // mx + b = cx + d
                // (m-c) x = d - b
                double x = (other.y_intercept - this.y_intercept) / (this.slope - other.slope);
                double y = evaluateY(x);
                return new ImmutableVector( x,  y);

            }

            @Override
            public boolean equals(Object obj)
            {
                if(obj instanceof Line)
                {
                    Line other = (Line) obj;
                    return other.slope == this.slope && other.y_intercept == this.y_intercept;
                }
                return false;
            }

            @Override
            public String toString() {
                return "Line{" +
                        "a=" + a +
                        ", b=" + b +
                        '}';
            }
        }
    }
}
