package com.github.ezauton.recorder.base.frame

import com.github.ezauton.recorder.SequentialDataFrame
import kotlinx.serialization.Serializable


@Serializable
class TankDriveableFrame(val time: Double, val attemptLeftVel: Double, val attemptRightVel: Double)
