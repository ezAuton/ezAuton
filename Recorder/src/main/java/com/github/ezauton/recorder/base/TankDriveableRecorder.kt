package com.github.ezauton.recorder.base

//import com.github.ezauton.recorder.SubRecording
//import com.github.ezauton.recorder.base.frame.TankDriveableFrame
//import kotlinx.serialization.Serializable

//@Serializable
//class TankDriveableRecorder(override val name: String, val frames: List<TankDriveableFrame>) : SubRecording

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
