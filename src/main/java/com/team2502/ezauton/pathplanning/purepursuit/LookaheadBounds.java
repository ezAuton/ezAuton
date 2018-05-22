package com.team2502.ezauton.pathplanning.purepursuit;

public class LookaheadBounds implements ILookaheadBounds
{

    private final double minDistance;
    private final double maxDistance;
    private final double minSpeed;

    private final double dDistance;
    private final double dSpeed;

    public LookaheadBounds(double minDistance, double maxDistance, double minSpeed, double maxSpeed)
    {
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        dDistance = maxDistance - minDistance;
        this.minSpeed = minSpeed;
        dSpeed = maxSpeed - minSpeed;
    }

    @Override
    public double getLookahead(double speed)
    {
        double lookahead = dDistance * (speed - minSpeed) / dSpeed + minDistance;
        return Double.isNaN(lookahead) ? minDistance : Math.max(minDistance, Math.min(maxDistance, lookahead));
    }
}
