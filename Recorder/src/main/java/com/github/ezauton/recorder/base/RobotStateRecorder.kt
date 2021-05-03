package com.github.ezauton.recorder.base

//import com.github.ezauton.recorder.SubRecording
//import com.github.ezauton.recorder.base.frame.RobotStateFrame
//import kotlinx.serialization.SerialName
//import kotlinx.serialization.Serializable
//
//
//@Serializable
//@SerialName("robotState")
//class RobotStateRecording(val frames: List<RobotStateFrame>, override val name: String) : SubRecording
//
//
//fun robotStateRecorder(posEstimator: TranslationalLocationEstimator, rotEstimator: RotationalLocationEstimator, width: Double, height: Double, period: PeriodicParams = PeriodicParams.DEFAULT) =
//
//
//  sendAction {
//    val frames =  periodic(period) { loop ->
//      RobotStateFrame(
//        posEstimator.estimateLocation().scalarVector,
//        rotEstimator.estimateHeading().value,
//        width,
//        height,
//        posEstimator.estimateAbsoluteVelocity().scalarVector,
//        loop.stopwatch.read().millis,
//      )
//    }
//
//    println("caught with!!!")
//
//    emit(RobotStateRecording(frames, "RobotStateRecording"))
//
//  }
