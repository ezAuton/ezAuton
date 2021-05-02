package com.github.ezauton.recorder.base

import com.github.ezauton.conversion.Distance
import com.github.ezauton.core.action.sendAction
import com.github.ezauton.core.pathplanning.Path
import com.github.ezauton.core.pathplanning.purepursuit.PurePursuitData
import com.github.ezauton.core.utils.Clock
import com.github.ezauton.core.utils.Stopwatch
import com.github.ezauton.recorder.SubRecording
import com.github.ezauton.recorder.base.frame.PurePursuitFrame
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


// TODO: add back Path
@Serializable
@SerialName("purePursuit")
class PurePursuitRecording(val frames: List<PurePursuitFrame>, override val name: String): SubRecording


fun purePursuitRecorder(clock: Clock, path: Path<Distance>, inputFlow: Flow<PurePursuitData>) = sendAction {
  val stopwatch = Stopwatch(clock)
  stopwatch.init()
  val dataFrames = ArrayList<PurePursuitFrame>()
  inputFlow.collect { data ->
    val time = stopwatch.read().millis
    val frame = PurePursuitFrame(time, data.lookahead.value, data.closestPoint, data.goalPoint.scalarVector, data.closestPointDist, data.currentSegmentIndex)
    dataFrames.add(frame)
  }

  emit(PurePursuitRecording(dataFrames, "PurePursuit"))

}
