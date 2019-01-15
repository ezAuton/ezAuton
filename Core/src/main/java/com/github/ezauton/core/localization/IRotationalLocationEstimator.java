package com.github.ezauton.core.localization;

/**
 * An interface for any class trying to estimate our heading
 */
public interface IRotationalLocationEstimator
{
    /**
     * @return The estimated heading of the robot in radians, where 0 is N, pi/2 is W...
     */
    double estimateHeading();
}
