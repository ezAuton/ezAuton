package com.team2502.ezauton.pathplanning;

import com.team2502.ezauton.trajectory.geometry.ImmutableVector;
import com.team2502.ezauton.utils.InterpolationMap;

/**
 * A fully-implemented linear path segment. This class
 * relies on finding motion states every dt and from this
 * using an interpolation map to see what desired motion states
 * should be for certain distances.
 */
public class PathSegmentExtrapolated extends LinearPathSegment
{

    private final double speedStart;
    private final double speedStop;
    private final double dt;
    private final double maxAccel;
    private final double maxDecel;
    private InterpolationMap speedInterpolator;

    /**
     * @param from
     * @param to
     * @param finish
     * @param beginning
     * @param distanceStart
     * @param speedStart
     * @param speedStop
     * @param dt            The difference in time should be extrapolated
     */
    protected PathSegmentExtrapolated(ImmutableVector from, ImmutableVector to, boolean finish, boolean beginning, double distanceStart, double speedStart, double speedStop, double dt, double maxAccel, double maxDecel)
    {
        super(from, to, finish, beginning, distanceStart);
        this.speedStart = speedStart;
        this.speedStop = speedStop;
        this.dt = dt;
        this.maxAccel = maxAccel;
        this.maxDecel = maxDecel;
        extrap();
    }

    private void extrap()
    {
        // You have probably seen: d_f = 1/2at^2 + vt + d_i
        // However, we are not having constant acceleration... so we need

        speedInterpolator = new InterpolationMap(0D, speedStart);

        double distance = getLength();
        double time = 0;
        if(speedStart < speedStop) // accel
        {
            MotionState motionState = new MotionState(0, speedStart, maxAccel, 0);
            while(motionState.getSpeed() < speedStop)
            {
                motionState = motionState.extrapolateTime(motionState.getTime() + dt);
                speedInterpolator.put(motionState.getPosition(), Math.min(speedStop, motionState.getSpeed()));
            }
        }
        else if(speedStart > speedStop) // decel
        {
            MotionState motionState = new MotionState(getLength(), speedStop, maxDecel, 0);
            while(motionState.getSpeed() < speedStart)
            {
                motionState = motionState.extrapolateTime(motionState.getTime() - dt);
                speedInterpolator.put(motionState.getPosition(), Math.min(speedStart, motionState.getSpeed()));
            }
        }
    }

    @Override
    public double getSpeed(double absoluteDistance)
    {
        return speedInterpolator.get(getRelativeDistance(absoluteDistance));
    }
}
