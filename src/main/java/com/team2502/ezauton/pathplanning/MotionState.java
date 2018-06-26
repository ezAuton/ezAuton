package com.team2502.ezauton.pathplanning;

import com.team2502.ezauton.utils.MathUtils;

import java.util.Collections;
import java.util.Set;

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
