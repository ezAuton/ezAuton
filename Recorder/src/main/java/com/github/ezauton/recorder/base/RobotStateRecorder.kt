package com.github.ezauton.recorder.base

import com.github.ezauton.core.action.PeriodicParams
import com.github.ezauton.core.action.periodic
import com.github.ezauton.core.action.sendAction
import com.github.ezauton.core.localization.RotationalLocationEstimator
import com.github.ezauton.core.localization.TranslationalLocationEstimator
import com.github.ezauton.recorder.base.frame.RobotStateFrame

suspend fun robotStateRecorder(period: PeriodicParams, posEstimator: TranslationalLocationEstimator, rotEstimator: RotationalLocationEstimator, width: Double, height: Double) = sendAction {
  periodic(period) { loop ->

    val frame = RobotStateFrame(
      loop.stopwatch.read().millis,
      posEstimator.estimateLocation().scalarVector,
      rotEstimator.estimateHeading().value,
      width,
      height,
      posEstimator.estimateAbsoluteVelocity().scalarVector
    )

    emit(frame)
  }
}
