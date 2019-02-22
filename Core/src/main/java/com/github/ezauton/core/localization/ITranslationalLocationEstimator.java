package com.github.ezauton.core.localization;

import com.github.ezauton.core.trajectory.geometry.ImmutableVector;

/**
 * An interface for any class trying to estimate our location
 */
public interface ITranslationalLocationEstimator {
    /**
     * Usually, in (x, y) coordinates where the y axis is parallel to the long side of the field, return our position
     *
     * @return Our position
     */
    ImmutableVector estimateLocation();

    /**
     * Usually, in (x, y) coordinates where the y axis is parallel to the long side of the field, return our current absolute velocity
     *
     * @return Our position
     */
    ImmutableVector estimateAbsoluteVelocity();
}
