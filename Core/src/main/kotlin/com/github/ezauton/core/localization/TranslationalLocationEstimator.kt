package com.github.ezauton.core.localization

import com.github.ezauton.conversion.ConcreteVector
import com.github.ezauton.conversion.Distance
import com.github.ezauton.conversion.Velocity

/**
 * An interface for any class trying to estimate our location
 */
interface TranslationalLocationEstimator {
    /**
     * Usually, in (x, y) coordinates where the y axis is parallel to the long side of the field, return our position
     *
     * @return Our position
     */
    fun estimateLocation(): ConcreteVector<Distance>

    /**
     * Usually, in (x, y) coordinates where the y axis is parallel to the long side of the field, return our current absolute velocity
     *
     * @return Our position
     */
    fun estimateAbsoluteVelocity(): ConcreteVector<Velocity>
}
