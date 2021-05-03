package com.github.ezauton.visualizer.processor

import com.github.ezauton.recorder.Recording
import com.github.ezauton.visualizer.processor.factory.DataProcessorFactory
import com.github.ezauton.visualizer.util.DataProcessor
import com.github.ezauton.visualizer.util.Environment
import javafx.animation.Interpolator
import javafx.animation.KeyValue

class RecordingDataProcessor(recording: Recording, factory: DataProcessorFactory) : DataProcessor {

  private val childDataProcessors = recording.recordingMap.values
    .asSequence()
    .map { subRecording -> factory.getProcessor(subRecording) }
    .filterNotNull()
    .toList()


  override fun initEnvironment(environment: Environment) {
    childDataProcessors.forEach {
      it.initEnvironment(environment)
    }
  }

  override fun generateKeyValues(interpolator: Interpolator): Map<Double, List<KeyValue>> {
    val ret: MutableMap<Double, MutableList<KeyValue>> = HashMap()
    for (dataProcessor in childDataProcessors) {
      val keyValMap = dataProcessor.generateKeyValues(interpolator)
      for ((key, value) in keyValMap) {
        if (!ret.containsKey(key)) // not contained
        {
          ret[key] = ArrayList(value)
        } else { // contained
          ret[key]!!.addAll(value)
        }
      }
    }
    return ret
  }

}
