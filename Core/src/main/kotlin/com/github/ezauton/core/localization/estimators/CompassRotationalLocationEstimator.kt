package com.github.ezauton.core.localization.estimators

import com.github.ezauton.core.localization.RotLocEst
import com.github.ezauton.core.localization.sensors.Compass

/**
 * TODO: fix... this seems redundant
 */
class CompassRotationalLocationEstimator(private val compass: Compass) : RotLocEst {

  override fun estimateHeading() = compass.angle

}
