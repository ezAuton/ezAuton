package com.github.ezauton.core.record

//import com.github.ezauton.recorder.base.PurePursuitRecording
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

//private val module = SerializersModule {
//  polymorphic(SubRecording::class) {
////    subclass(PurePur::class)
//    subclass(RobotStateRecording::class)
//    subclass(TankDriveableRecorder::class)
//  }
//}

val format = Json {}
