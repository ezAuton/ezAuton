package com.github.ezauton.visualizer

import com.github.ezauton.conversion.svec
import javafx.beans.property.SimpleDoubleProperty
import javafx.scene.input.MouseEvent
import tornadofx.*


class MyView : View() {


  private val originXProperty = SimpleDoubleProperty(0.0)
  private val originYProperty = SimpleDoubleProperty(0.0)

  private var originX by originXProperty
  private var originY by originYProperty

  private var originBefore = svec(0, 0)
  private var mouseBefore = svec(0, 0)

  override val root = vbox {

    textfield(originXProperty)

    addEventFilter(MouseEvent.MOUSE_PRESSED) { e ->
      mouseBefore = svec(e.x, e.y)
      originBefore = svec(originX, originY)
    }

    addEventFilter(MouseEvent.MOUSE_DRAGGED) { e ->
      val mouseNow = svec(e.x, e.y)
      val diff = mouseNow - mouseBefore

      originX = originBefore.x - diff.x
      originY = originBefore.y - diff.y

    }

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
