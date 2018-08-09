package com.team2502.ezauton.pathplanning.purepursuit;

import com.team2502.ezauton.localization.ITankRobotVelocityEstimator;

public class LookaheadBounds implements ILookahead
{

    private final double minDistance;
    private final double maxDistance;
    private final double minSpeed;

    private final double dDistance;
    private final double dSpeed;
    private final ITankRobotVelocityEstimator velocityEstimator;

    public LookaheadBounds(double minDistance, double maxDistance, double minSpeed, double maxSpeed, ITankRobotVelocityEstimator velocityEstimator)
    {
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        dDistance = maxDistance - minDistance;
        this.minSpeed = minSpeed;
        dSpeed = maxSpeed - minSpeed;
        this.velocityEstimator = velocityEstimator;
    }

    @Override
    public double getLookahead()
    {
        double speed = velocityEstimator.getAvgTranslationalWheelSpeed();
        double lookahead = dDistance * (speed - minSpeed) / dSpeed + minDistance;
        return Double.isNaN(lookahead) ? minDistance : Math.max(minDistance, Math.min(maxDistance, lookahead));
    }
}
