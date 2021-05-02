package com.github.ezauton.recorder.base

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.ezauton.conversion.Distance
import com.github.ezauton.core.action.sendAction
import com.github.ezauton.core.pathplanning.Path
import com.github.ezauton.core.pathplanning.purepursuit.PurePursuitData
import com.github.ezauton.core.utils.Clock
import com.github.ezauton.core.utils.Stopwatch
import com.github.ezauton.recorder.SeqRecording
import com.github.ezauton.recorder.base.frame.PurePursuitFrame
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

class PurePursuitRecording(@JsonProperty private val path: Path<Distance>, frames: List<PurePursuitFrame>) : SeqRecording<PurePursuitFrame>("PurePursuitRecording", frames)


suspend fun purePursuitRecorder(name: String, clock: Clock, path: Path<Distance>, inputFlow: Flow<PurePursuitData>) = sendAction {
  val stopwatch = Stopwatch(clock)
  stopwatch.init()
  val dataFrames = ArrayList<PurePursuitFrame>()
  inputFlow.collect { data ->
    val time = stopwatch.read().millis
    val frame = PurePursuitFrame(time, data.lookahead.value, data.closestPoint, data.goalPoint.scalarVector, data.closestPointDist, data.currentSegmentIndex)
    dataFrames.add(frame)
  }

  emit(PurePursuitRecording(path, dataFrames))

}
