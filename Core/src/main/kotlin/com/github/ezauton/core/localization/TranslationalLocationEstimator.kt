package com.github.ezauton.core.localization

import com.github.ezauton.core.trajectory.geometry.ImmutableVector

/**
 * An interface for any class trying to estimate our location
 */
interface TranslationalLocationEstimator {
    /**
     * Usually, in (x, y) coordinates where the y axis is parallel to the long side of the field, return our position
     *
     * @return Our position
     */
    fun estimateLocation(): ImmutableVector

    /**
     * Usually, in (x, y) coordinates where the y axis is parallel to the long side of the field, return our current absolute velocity
     *
     * @return Our position
     */
    fun estimateAbsoluteVelocity(): ImmutableVector
}
