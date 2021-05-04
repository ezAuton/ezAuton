package com.github.ezauton.visualizer

import javafx.scene.input.MouseEvent
import tornadofx.*

class MyView : View() {

  var originX = 0.0
  var originY = 0.0

  override val root = vbox {
    addEventFilter(MouseEvent.MOUSE_PRESSED, ::startDrag)
    addEventFilter(MouseEvent.MOUSE_DRAGGED, ::animateDrag)
    addEventFilter(MouseEvent.MOUSE_RELEASED, ::stopDrag)
    addEventFilter(MouseEvent.MOUSE_RELEASED, ::drop)
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
