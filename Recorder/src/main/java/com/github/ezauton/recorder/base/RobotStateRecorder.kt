package com.github.ezauton.recorder.base

import com.github.ezauton.core.action.PeriodicParams
import com.github.ezauton.core.action.periodic
import com.github.ezauton.core.action.sendAction
import com.github.ezauton.core.localization.RotationalLocationEstimator
import com.github.ezauton.core.localization.TranslationalLocationEstimator
import com.github.ezauton.recorder.SeqRecording
import com.github.ezauton.recorder.base.frame.RobotStateFrame


class RobotStateRecording(frames: List<RobotStateFrame>) : SeqRecording<RobotStateFrame>("RobotStateRecording", frames)

suspend fun robotStateRecorder(period: PeriodicParams, posEstimator: TranslationalLocationEstimator, rotEstimator: RotationalLocationEstimator, width: Double, height: Double) = sendAction {
  val frames = periodic(period) { loop ->
    RobotStateFrame(
      posEstimator.estimateLocation().scalarVector,
      rotEstimator.estimateHeading().value,
      width,
      height,
      posEstimator.estimateAbsoluteVelocity().scalarVector,
      loop.stopwatch.read().millis,
    )
  }

  emit(RobotStateRecording(frames))
}
