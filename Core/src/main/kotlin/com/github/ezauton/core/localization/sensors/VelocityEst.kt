package com.github.ezauton.core.localization.sensors

import com.github.ezauton.conversion.LinearVelocity

interface VelocityEst {
  val translationalVelocity: LinearVelocity
}
