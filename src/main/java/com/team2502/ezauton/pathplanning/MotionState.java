package com.team2502.ezauton.pathplanning;

import com.team2502.ezauton.utils.MathUtils;

import java.util.Collections;
import java.util.Set;

public class MotionState
{

    private final double position;
    private final double velocity;
    private final double acceleration;
    private final double time;

    public MotionState(double position, double velocity, double acceleration, double time)
    {
        this.position = position;
        this.velocity = velocity;
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

    public double getVelocity()
    {
        return velocity;
    }

    public double getAcceleration()
    {
        return acceleration;
    }

    public MotionState extrapolate(double time)
    {
        double dt = time - this.time;
        return new MotionState(position+velocity*dt+1/2D*acceleration*dt*dt,
                               velocity+ acceleration * dt, acceleration,time);
    }

    public double timeByPos(double position)
    {
        Set<Double> solutions = MathUtils.Algebra.quadratic(1 / 2D * acceleration, velocity, this.position - position);
        solutions.removeIf(val -> val < 0);
        if(solutions.size() == 0)
        {
            return Double.NaN;
        }
        return Collections.min(solutions)+time;
    }

}
