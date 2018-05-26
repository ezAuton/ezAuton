package com.team2502.ezauton.pathplanning;

import com.team2502.ezauton.utils.MathUtils;

public class MotionSegment
{

    private final MotionState from;
    private final MotionState to;

    public MotionSegment(MotionState from, MotionState to)
    {
        this.from = from;
        this.to = to;
    }

    public boolean containsPos(double pos)
    {
        return MathUtils.Algebra.bounded(from.getPosition(),pos,to.getPosition());
    }
    public MotionState getFrom()
    {
        return from;
    }

    public MotionState getTo()
    {
        return to;
    }
}
