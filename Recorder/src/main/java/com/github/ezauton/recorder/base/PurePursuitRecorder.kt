package com.github.ezauton.recorder.base

import com.github.ezauton.conversion.Distance
import com.github.ezauton.core.pathplanning.Path
import com.github.ezauton.core.pathplanning.purepursuit.PurePursuitData
import com.github.ezauton.core.utils.Clock
import com.github.ezauton.core.utils.Stopwatch
import com.github.ezauton.recorder.base.frame.PurePursuitFrame
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


fun purePursuitRecorder(inputFlow: Flow<PurePursuitData>) = inputFlow.map { data ->
    PurePursuitFrame(time, data.lookahead.value, data.closestPoint, data.goalPoint.scalarVector, data.closestPointDist, data.currentSegmentIndex)
}
