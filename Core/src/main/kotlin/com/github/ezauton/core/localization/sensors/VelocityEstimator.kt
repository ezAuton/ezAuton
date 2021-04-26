package com.github.ezauton.core.localization.sensors

import com.github.ezauton.conversion.LinearVelocity

interface VelocityEstimator {
  val translationalVelocity: LinearVelocity
}
