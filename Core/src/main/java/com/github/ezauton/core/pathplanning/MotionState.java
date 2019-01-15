package com.github.ezauton.core.pathplanning;

import com.github.ezauton.core.utils.MathUtils;

import java.util.Collections;
import java.util.Set;

/**
 * Contains the pose of the robot at a certain time and distance. This class provides useful tools
 * for extrapolating future/previous MotionStates based on distances/times.
 */
//TODO: Make a subclass for the purposes of PP Logging, ala RC2018:PurePursuitFrame
public class MotionState
{

    private final double position;
    private final double speed;
    private final double acceleration;
    private final double time;


    public MotionState(double position, double speed, double acceleration, double time)
    {
        this.position = position;
        this.speed = speed;
        this.acceleration = acceleration;
        this.time = time;
    }

    public double getTime()
    {
        return time;
    }

    public double getPosition()
    {
        return position;
    }

    public double getSpeed()
    {
        return speed;
    }

    public double getAcceleration()
    {
        return acceleration;
    }

    /**
     * @param time
     * @return The future Motion State given a time
     */
    public MotionState extrapolateTime(double time)
    {
        double dt = time - this.time;
        return new MotionState(position + speed * dt + 1 / 2D * acceleration * dt * dt,
                               speed + acceleration * dt, acceleration, time);
    }

    /**
     * Return a copy of this object, but with a different acceleration value
     *
     * @param a The new acceleration value
     * @return This, but with the different accel value
     */
    public MotionState forAcceleration(double a)
    {
        return new MotionState(position, speed, a, time);
    }

    /**
     * @param pos
     * @return The future Motion State given a pos
     */
    public MotionState extrapolatePos(double pos)
    {
        return extrapolateTime(timeByPos(position));
    }

    /**
     * @param position
     * @return The time it will be given a position by extrapolation
     */
    public double timeByPos(double position)
    {
        Set<Double> solutions = MathUtils.Algebra.quadratic(1 / 2D * acceleration, speed, this.position - position);
        solutions.removeIf(val -> val < 0);
        if(solutions.size() == 0)
        {
            return Double.NaN;
        }
        return Collections.min(solutions) + time;
    }

}
