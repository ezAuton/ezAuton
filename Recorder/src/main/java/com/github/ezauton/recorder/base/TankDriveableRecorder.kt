package com.github.ezauton.recorder.base

import com.github.ezauton.core.action.PeriodicParams
import com.github.ezauton.core.action.periodic
import com.github.ezauton.core.action.sendAction
import com.github.ezauton.core.robot.implemented.TankRobotTransLocDrivable
import com.github.ezauton.recorder.SubRecording
import com.github.ezauton.recorder.base.frame.TankDriveableFrame
import kotlinx.serialization.Serializable

@Serializable
class TankDriveableRecorder(override val name: String, val frames: List<TankDriveableFrame>) : SubRecording

//fun tankDrivableRecorder(transLocDriveable: TankRobotTransLocDrivable, periodicParams: PeriodicParams = PeriodicParams.DEFAULT) = sendAction {
//  val frames = periodic(periodicParams.copy(catchWith = true)) { loop ->
//    TankDriveableFrame(
//      loop.stopwatch.read().millis,
//      transLocDriveable.lastLeftTarget.value,
//      transLocDriveable.lastRightTarget.value
//    )
//  }
//
//  println("caught with!!!")
//
//  emit(TankDriveableRecorder("TankDrivableRecorder", frames))
//}
