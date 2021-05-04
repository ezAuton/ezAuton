package com.github.ezauton.visualizer

import com.github.ezauton.core.record.Recording
import com.github.ezauton.core.record.realisticFlow
import com.github.ezauton.visualizer.controller.State
import com.github.ezauton.visualizer.view.Birdseye
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import tornadofx.*


class Test : View() {

  private val controller: State by inject()

  override val root = stackpane {
    button("Choose File") {
      action {
        val file = chooseJsonFile() ?: return@action
        val recording = try {
          Recording.load(file)
        } catch (e: SerializationException) {
          System.err.println("could not parse the given file")
          return@action
        }

        GlobalScope.launch {
          controller.insertData(recording.samples.realisticFlow())
        }




      }
    }
  }

}

class MyView : View() {


  override val root = borderpane {
    top<Birdseye>()
    bottom<Test>()
  }

}

class VisualizerApp : App(MyView::class) {

}

//class Visualizer : Application() {
//  private lateinit var mainScene: Scene
//
//  private val mainFxml by lazy {
//    requireNotNull(resource("main.fxml"))
//  }
//
//  }
//
//  @Throws(Exception::class)
//  override fun start(primaryStage: Stage) {
//    instance = this
//
//    // Keep a reference to the window
//    primaryStage.icons.add(icon)
//    primaryStage.title = "PP Player"
//    val mainRoot: Parent = FXMLLoader.load(mainFxml)
//
//    // Display the window
//    mainScene = Scene(mainRoot)
//    primaryStage.scene = mainScene
//    primaryStage.show()
//  }
//
//  val stage: Window get() = mainScene.window
//
//
//  companion object {
//    lateinit var instance: Visualizer
//      private set
//
//    @JvmStatic
//    fun main(args: Array<String>) {
//      launch(Visualizer::class.java, *args);
//    }
//  }
//}
