package com.github.ezauton.visualizer.processor

import javafx.animation.Interpolator
import com.github.ezauton.visualizer.util.DataProcessor
import java.util.HashMap
import com.github.ezauton.recorder.base.TankDriveableRecorder
import com.github.ezauton.visualizer.util.Environment
import javafx.animation.KeyValue
import javafx.scene.control.*

class TankDriveableDataProcessor(private val recorder: TankDriveableRecorder) : DataProcessor {

  private lateinit var leftVel: Label
  private lateinit var rightVel: Label

  override fun initEnvironment(environment: Environment) {
    // heading info
    leftVel = Label("0")

    // x y loc
    rightVel = Label("0")
    environment.getDataGridPane(recorder.name).addRow(0, Label("Left vel: "), leftVel)
    environment.getDataGridPane(recorder.name).addRow(1, Label("Right vel: "), rightVel)
  }

  override fun generateKeyValues(interpolator: Interpolator): Map<Double, List<KeyValue>> {
    val map: MutableMap<Double, List<KeyValue>> = HashMap()
    recorder.frames
      .forEach { frame ->
        val kv1 = KeyValue(leftVel.textProperty(), String.format("%.02f", frame.attemptLeftVel))
        val kv2 = KeyValue(rightVel.textProperty(), String.format("%.02f", frame.attemptRightVel))
        map[frame.time] = listOf(kv1, kv2)
      }
    return map
  }
}
