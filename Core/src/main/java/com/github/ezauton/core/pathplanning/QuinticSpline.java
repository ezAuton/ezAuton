package com.github.ezauton.core.pathplanning;


import com.github.ezauton.core.pathplanning.purepursuit.PPWaypoint;
import com.github.ezauton.core.trajectory.geometry.ImmutableVector;
import com.github.ezauton.core.utils.InterpolationMap;
import com.github.ezauton.core.utils.MathUtils;

import java.util.ArrayList;
import java.util.List;

public class QuinticSpline implements MathUtils.Geometry.ParametricFunction {
    private static final double FEET_PER_SUBDIVISION = 1 / 3D;
    private final ImmutableVector first;
    private final ImmutableVector last;

    private final ImmutableVector firstSlope;
    private final ImmutableVector lastSlope;

    /**
     * t^5 coefficient for the quintic spline equation
     */
    private final ImmutableVector a;

    /**
     * t^4 coefficient for the quintic spline equation
     */
    private final ImmutableVector b;

    /**
     * t^3 coefficient for the quintic spline equation
     */
    private final ImmutableVector c;

    /**
     * t^2 coefficient for the quintic spline equation
     */
    private final ImmutableVector d;

    /**
     * t^1 coefficient for the quintic spline equation
     */
    private final ImmutableVector e;

    /**
     * t^0 coefficient for the quintic spline equation
     */
    private final ImmutableVector f;


    public QuinticSpline(ImmutableVector first, ImmutableVector last, ImmutableVector firstSlope, ImmutableVector lastSlope) {
        this.first = first;
        this.last = last;

        this.firstSlope = firstSlope;
        this.lastSlope = lastSlope;

        // -3 (2p_0 - 2p_1 + p'_0 + p'_1)
        a = (first.mul(2)
                .sub(last.mul(2))
                .add(firstSlope)
                .add(lastSlope)
        ).mul(-3);


        //  15 p_0 - 15 p_1 + 8p'_0 + 7p'_1
        b = first.mul(15)
                .sub(last.mul(15))
                .add(firstSlope.mul(8))
                .add(lastSlope.mul(7));

        // -2 ( 5p_0 - 5p_1 + 3p'_0 + 2p'_1 )
        c = (first.mul(5)
                .sub(last.mul(5))
                .add(firstSlope.mul(3))
                .add(lastSlope.mul(2))
        ).mul(-2);

        // 0
        d = new ImmutableVector(0, 0);

        // p'_0
        e = firstSlope;

        // p_0
        f = first;

    }

    public QuinticSpline(ImmutableVector first, ImmutableVector last, double firstTheta, double lastTheta) {
        this(first,
                last,
                new ImmutableVector(Math.cos(firstTheta), Math.sin(firstTheta)).mul(1.2 * first.dist(last)),
                new ImmutableVector(Math.cos(lastTheta), Math.sin(lastTheta)).mul(1.2 * first.dist(last))
        );
    }

    /**
     * Using a few linear path segments (for motion information) and a few splines (for location information), create lots of small linear path segments
     * in the shape of the splines.
     *
     * @param splines  A list of splines
     * @param segments A list of path segments
     * @return An array of spline-shaped path segments
     */
    public static PPWaypoint[] toPathSegments(List<QuinticSpline> splines, List<? extends PPWaypoint> segments) {
        boolean isError = false;

        // check if the spline points don't match up with the path waypoints

        try {
            for (int i = 0; i < splines.size(); i++) {
                if (i < segments.size()) {
                    boolean hasSameStartingPoint = splines.get(i).first.equals(segments.get(i).getLocation());
                    boolean hasSameEndingPoint = splines.get(i).last.equals(segments.get(i + 1).getLocation());
                    if (!hasSameStartingPoint || !hasSameEndingPoint) {
                        isError = true;
                        break;
                    }
                } else {
                    isError = true;
                    break;
                }
            }

            if (isError) {
                //TODO: More descriptive error message
                throw new RuntimeException("your splines don't intersect your path at the right spots (the waypoints");
            }

        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeException("your splines don't intersect your path at the right spots (the waypoints", e);
        }

        List<PPWaypoint> retList = new ArrayList<>();

        for (int s = 0; s < splines.size(); s++) {
            PPWaypoint fromWaypoint = segments.get(s);
            PPWaypoint toWaypoint = segments.get(s + 1);
            QuinticSpline currentSpline = splines.get(s);

            // Maps t to speed
            InterpolationMap speedMap = new InterpolationMap(0D, fromWaypoint.getSpeed());
            speedMap.put(1D, toWaypoint.getSpeed());

            InterpolationMap accelMap = new InterpolationMap(0D, fromWaypoint.getAcceleration());
            accelMap.put(1D, toWaypoint.getAcceleration());

            InterpolationMap decelMap = new InterpolationMap(0D, fromWaypoint.getDeceleration());
            decelMap.put(1D, toWaypoint.getDeceleration());

            int numSubdivisions = (int) (splines.get(s).getLength() / FEET_PER_SUBDIVISION); // subdivision approximately every 4 inches
            double tInterval = 1D / numSubdivisions;
            double end = s == splines.size() - 1 ? 1 : 1 - tInterval;
            for (double t = 0; t < end; t += tInterval) {
                ImmutableVector loc = currentSpline.get(t);
                PPWaypoint newWaypoint = new PPWaypoint(loc, speedMap.get(t), fromWaypoint.getAcceleration(), fromWaypoint.getDeceleration());
                retList.add(newWaypoint);
            }
        }
        PPWaypoint[] retArray = new PPWaypoint[retList.size()];
        retList.toArray(retArray);
        return retArray;

    }

    public ImmutableVector getPoint(double relativeDistance) {
        return fromArcLength(relativeDistance);
    }

    public double getDistanceLeft2(ImmutableVector point) {
        return MathUtils.pow2(getDistanceLeft(point));
    }

    public double getDistanceLeft(ImmutableVector point) {
        return (getArcLength(0, 1) - getArcLength(0, getT(point, 0, 1)));
    }

    public double getLength() {
        return getArcLength(0, 1);
    }

    public String toString() {
        return "QuinticSpline{" +
                "first=" + first +
                ", last=" + last +
                ", firstSlope=" + firstSlope +
                ", lastSlope=" + lastSlope +
                '}';
    }

    /**
     * @param t:[0,1]
     * @return
     */
    public double getX(double t) {
        return MathUtils.pow5(t) * a.get(0) + MathUtils.pow4(t) * b.get(0) + MathUtils.pow3(t) * c.get(0) + MathUtils.pow2(t) * d.get(0) + t * e.get(0) + f.get(0);
    }

    /**
     * @param t:[0,1]
     * @return
     */
    public double getY(double t) {
        return MathUtils.pow5(t) * a.get(1) + MathUtils.pow4(t) * b.get(1) + MathUtils.pow3(t) * c.get(1) + MathUtils.pow2(t) * d.get(1) + t * e.get(1) + f.get(1);
    }

    public ImmutableVector getLastSlope() {
        return lastSlope;
    }

    public ImmutableVector getFirstSlope() {
        return firstSlope;
    }

    public ImmutableVector getFirst() {
        return first;
    }

    public ImmutableVector getLast() {
        return last;
    }

}
