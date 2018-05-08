package com.team2502.ezauton.localization;

import org.joml.ImmutableVector;

/**
 * An interface for any class trying to estimate our location
 */
public interface ITranslationalLocationEstimator
{
    /**
     * In (x, y) coordinates where the y axis is parallel to the long side of the field, return our position
     *
     * @return Our position
     */
    ImmutableVector estimateLocation();
}
