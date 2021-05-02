package com.github.ezauton.recorder.base.frame

import com.github.ezauton.conversion.ScalarVector
import kotlinx.serialization.Serializable

@Serializable
class PurePursuitFrame(val time: Double, val lookahead: Double, val closestPoint: ScalarVector, val goalPoint: ScalarVector, private val dCP: Double, val currentSegmentIndex: Int)
