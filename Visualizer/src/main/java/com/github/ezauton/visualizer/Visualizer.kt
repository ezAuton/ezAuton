package com.github.ezauton.visualizer

import com.github.ezauton.recorder.Recording
import com.github.ezauton.recorder.base.PurePursuitRecording
import com.github.ezauton.recorder.base.RobotStateRecording
import com.github.ezauton.recorder.base.TankDriveableRecorder
import com.github.ezauton.visualizer.processor.PurePursuitDataProcessor
import com.github.ezauton.visualizer.processor.RecordingDataProcessor
import com.github.ezauton.visualizer.processor.RobotStateDataProcessor
import com.github.ezauton.visualizer.processor.TankDriveableDataProcessor
import com.github.ezauton.visualizer.processor.factory.FactoryMap
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage
import javafx.stage.Window
import java.net.URL


class Visualizer : Application() {
  val factory: FactoryMap = FactoryMap()
  private lateinit var mainScene: Scene

  val mainFxml
    get(): URL {
      val inner = javaClass.getResource("main.fxml")
      requireNotNull(inner)
      return inner
    }

  @Throws(Exception::class)
  override fun start(primaryStage: Stage) {
    instance = this
    factory.register<PurePursuitRecording> { ppRec -> PurePursuitDataProcessor(ppRec) }
    factory.register<RobotStateRecording> { robotRec -> RobotStateDataProcessor(robotRec) }
    factory.register<TankDriveableRecorder> { recorder -> TankDriveableDataProcessor(recorder) }
    factory.register<Recording> { recording -> RecordingDataProcessor(recording, factory) }


    // Keep a reference to the window
    primaryStage.icons.add(Image(javaClass.getResourceAsStream("icon.png")))
    primaryStage.title = "PP Player"
    val mainRoot: Parent = FXMLLoader.load(mainFxml)

    // Display the window
    mainScene = Scene(mainRoot)
    primaryStage.scene = mainScene
    primaryStage.show()
  }

  val stage: Window get() = mainScene.window


  companion object {
    lateinit var instance: Visualizer
      private set

    @JvmStatic
    fun main(args: Array<String>) {
      launch(Visualizer::class.java, *args);
    }
  }
}
