package com.github.ezauton.core.pathplanning.purepursuit;

import com.github.ezauton.core.localization.sensors.VelocityEstimator;

/**
 * Easy lookahead implementation given a speed. Takes in min speed, max speed, min lookahead, max lookahead and
 * performs a linear interpolation.
 */
public class LookaheadBounds implements Lookahead {

    private final double minDistance;
    private final double maxDistance;
    private final double minSpeed;

    private final double dDistance;
    private final double dSpeed;

    /**
     * Create some lookahead bounds
     *
     * @param minDistance       Minimum lookahead
     * @param maxDistance       Maximum lookahead
     * @param minSpeed          Minimum speed where lookahead is allowed to be dynamic
     * @param maxSpeed          Maximum speed where lookahead is allowed to be dynamic
     */
    public LookaheadBounds(double minDistance, double maxDistance, double minSpeed, double maxSpeed) {
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        dDistance = maxDistance - minDistance;
        this.minSpeed = minSpeed;
        dSpeed = maxSpeed - minSpeed;
    }

    /**
     * Based on the current spead as described by this.velocityEstimator, calculate a lookahead
     *
     * @return The lookahead to use
     */
    @Override
    public double getLookahead(VelocityEstimator velocityEstimator) {
        double speed = Math.abs(velocityEstimator.getTranslationalVelocity());
        double lookahead = dDistance * (speed - minSpeed) / dSpeed + minDistance;
        return Double.isNaN(lookahead) ? minDistance : Math.max(minDistance, Math.min(maxDistance, lookahead));
    }
}
