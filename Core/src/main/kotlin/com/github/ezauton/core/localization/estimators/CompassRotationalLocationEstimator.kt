package com.github.ezauton.core.localization.estimators

import com.github.ezauton.core.localization.RotationalLocationEstimator
import com.github.ezauton.core.localization.sensors.Compass

class CompassRotationalLocationEstimator(private val compass: Compass) : RotationalLocationEstimator {

    override fun estimateHeading(): Double {
        return compass.radians
    }
}
