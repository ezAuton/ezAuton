package com.github.ezauton.core.localization.estimators

import com.github.ezauton.core.localization.RotationalLocationEstimator
import com.github.ezauton.core.localization.sensors.Compass

/**
 * TODO: fix... this seems redundant
 */
class CompassRotationalLocationEstimator(private val compass: Compass) : RotationalLocationEstimator {

    override fun estimateHeading() = compass.angle

}
