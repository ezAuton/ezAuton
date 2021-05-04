package com.github.ezauton.visualizer.view

import com.github.ezauton.conversion.svec
import com.github.ezauton.visualizer.controller.State
import javafx.geometry.Insets
import javafx.scene.input.MouseEvent
import tornadofx.*

class Birdseye : View() {

  private var originBefore = svec(0, 0)
  private var mouseBefore = svec(0, 0)

  private val controller: State by inject()

  override val root = vbox {

    useMaxHeight = true

    padding = Insets(0.0, 0.0, 20.0, 0.0)

    textfield(controller.originXProperty)


    addEventFilter(MouseEvent.MOUSE_PRESSED) { e ->
      println("mouse pressed")
      mouseBefore = svec(e.x, e.y)
      originBefore = svec(controller.originX, controller.originY)
    }

    addEventFilter(MouseEvent.MOUSE_DRAGGED) { e ->
      println("mouse dragged")
      val mouseNow = svec(e.x, e.y)
      val diff = mouseNow - mouseBefore

      controller.originX = originBefore.x - diff.x
      controller.originY = originBefore.y - diff.y

    }


  }

}
