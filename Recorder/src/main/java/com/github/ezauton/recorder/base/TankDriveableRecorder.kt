package com.github.ezauton.recorder.base

import com.github.ezauton.core.action.PeriodicParams
import com.github.ezauton.core.action.periodic
import com.github.ezauton.core.action.sendAction
import com.github.ezauton.recorder.base.frame.TankDriveableFrame
import com.github.ezauton.core.robot.implemented.TankRobotTransLocDrivable

suspend fun tankDrivableRecorder(periodicParams: PeriodicParams, transLocDriveable: TankRobotTransLocDrivable) = sendAction {
  periodic(periodicParams){ loop ->
    val frame = TankDriveableFrame(
      loop.stopwatch.read().millis,
      transLocDriveable.lastLeftTarget.value,
      transLocDriveable.lastRightTarget.value
    )

    emit(frame)

  }
}
