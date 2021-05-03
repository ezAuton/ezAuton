package com.github.ezauton.visualizer.util

import javafx.animation.Interpolator
import javafx.animation.KeyValue

interface DataProcessor {
  fun initEnvironment(environment: Environment)
  fun generateKeyValues(interpolator: Interpolator): Map<Double, List<KeyValue>>
}
