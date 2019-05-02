package com.github.ezauton.core.localization.sensors

import com.github.ezauton.conversion.Velocity

interface VelocityEstimator {
    val translationalVelocity: Velocity
}
