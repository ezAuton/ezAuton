package com.github.ezauton.core.pathplanning;

import com.github.ezauton.core.trajectory.geometry.ImmutableVector;
import com.github.ezauton.core.utils.InterpolationMap;

/**
 * A fully-implemented linear path segment. This class
 * relies on finding motion states every dt and from this
 * using an interpolation map to see what desired motion states
 * should be for certain distances.
 */
public class PathSegmentInterpolated extends LinearPathSegment
{

    private double speedStart;
    private double speedStop;
    private double dt;
    private double maxAccel;
    private double maxDecel;
    private InterpolationMap speedInterpolator;

    /**
     * @param from          Starting location of the path segment
     * @param to            Ending location of the path segment
     * @param finish        If this is the last path segment
     * @param beginning     If this is the first path segment
     * @param distanceStart Distance along the path from the beginning to <code>from</code>
     * @param speedStart    Target speed to go at the start of the path
     * @param speedStop     Target speed to go at the end of the path
     * @param dt            The difference in time should be extrapolated
     */
    protected PathSegmentInterpolated(ImmutableVector from, ImmutableVector to, boolean finish, boolean beginning, double distanceStart, double speedStart, double speedStop, double dt, double maxAccel, double maxDecel)
    {
        super(from, to, finish, beginning, distanceStart);
        this.speedStart = speedStart;
        this.speedStop = speedStop;
        this.dt = dt;
        this.maxAccel = maxAccel;
        this.maxDecel = maxDecel;
        extrap();
    }

    private PathSegmentInterpolated() {}

    public InterpolationMap getSpeedInterpolator()
    {
        return speedInterpolator;
    }

    /**
     * Build this.speedInterpolator
     */
    private void extrap()
    {
        // You have probably seen: d_f = 1/2at^2 + vt + d_i
        // However, we are not having constant acceleration... so we need

        // Make extrapolation for speed
        speedInterpolator = new InterpolationMap(0D, speedStart);

        // Use kinematics equations built into the MotionState class to build speedInterpolator
        if(speedStart < speedStop) // accel
        {
            MotionState motionState = new MotionState(0, speedStart, maxAccel, 0);
            while(motionState.getSpeed() < speedStop)
            {
                motionState = motionState.extrapolateTime(motionState.getTime() + dt);
                double position = motionState.getPosition();
                if(position > getLength())
                {
                    double velLeft = speedStop - motionState.getSpeed();
                    if(velLeft < 0) return;
                    String msg = String.format("Acceleration value too low to execute trajectory from %s To: %s. At max accelerate still needed to accelerate: %.2f", getFrom(), getTo(), velLeft);
                    throw new IllegalStateException(msg);
                }
                speedInterpolator.put(position, Math.min(speedStop, motionState.getSpeed()));
            }
        }
        else if(speedStart > speedStop) // decel
        {
            MotionState motionState = new MotionState(getLength(), speedStop, maxDecel, 0);
            speedInterpolator.put(getLength(), speedStop);
            while(motionState.getSpeed() < speedStart)
            {
                motionState = motionState.extrapolateTime(motionState.getTime() - dt);
                double position = motionState.getPosition();
                if(position < 0)
                {
                    double velLeft = speedStart - motionState.getSpeed();
                    if(velLeft < 0) return;
                    String msg = String.format("Deceleration (magnitude) value too low to execute trajectory from %s to %s. At max deceleration still needed to decelerate: %.2f", getFrom(), getTo(), velLeft);
                    throw new IllegalStateException(msg);
                }
                speedInterpolator.put(position, Math.min(speedStart, motionState.getSpeed()));
            }
        }
    }

    @Override
    public double getSpeed(double absoluteDistance)
    {
        double relativeDistance = getRelativeDistance(absoluteDistance);
        return speedInterpolator.get(relativeDistance);
    }
}
