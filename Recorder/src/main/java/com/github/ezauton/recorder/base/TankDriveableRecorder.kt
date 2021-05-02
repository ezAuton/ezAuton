package com.github.ezauton.recorder.base

import com.github.ezauton.core.action.PeriodicParams
import com.github.ezauton.core.action.periodic
import com.github.ezauton.core.action.sendAction
import com.github.ezauton.core.robot.implemented.TankRobotTransLocDrivable
import com.github.ezauton.recorder.SeqRecording
import com.github.ezauton.recorder.base.frame.TankDriveableFrame

suspend fun tankDrivableRecorder(periodicParams: PeriodicParams, transLocDriveable: TankRobotTransLocDrivable) = sendAction {
  val frames = periodic(periodicParams) { loop ->
    TankDriveableFrame(
      loop.stopwatch.read().millis,
      transLocDriveable.lastLeftTarget.value,
      transLocDriveable.lastRightTarget.value
    )
  }

  emit(SeqRecording.of("TankDrivableRecorder", frames))
}
