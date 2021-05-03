package com.github.ezauton.visualizer.processor.factory

import com.github.ezauton.recorder.SubRecording
import com.github.ezauton.visualizer.util.DataProcessor

interface DataProcessorFactory {
  fun getProcessor(subRecording: SubRecording): DataProcessor?
}
