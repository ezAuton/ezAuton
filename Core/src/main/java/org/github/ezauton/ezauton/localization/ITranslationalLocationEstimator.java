package org.github.ezauton.ezauton.localization;

import org.github.ezauton.ezauton.trajectory.geometry.ImmutableVector;

/**
 * An interface for any class trying to estimate our location
 */
//TODO: Suggestion -- Rename to ILocationEstimator to reduce redundancy in the name -- rm
public interface ITranslationalLocationEstimator
{
    /**
     * In (x, y) coordinates where the y axis is parallel to the long side of the field, return our position
     *
     * @return Our position
     */
    ImmutableVector estimateLocation();

    /**
     * In (x, y) coordinates where the y axis is parallel to the long side of the field, return our current absolute velocity
     *
     * @return Our position
     */
    ImmutableVector estimateAbsoluteVelocity();
}
