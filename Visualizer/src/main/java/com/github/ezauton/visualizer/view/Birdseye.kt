package com.github.ezauton.visualizer.view

import com.github.ezauton.conversion.svec
import com.github.ezauton.core.record.Data
import com.github.ezauton.visualizer.CSS_PATH
import com.github.ezauton.visualizer.controller.State
import javafx.beans.property.SimpleDoubleProperty
import javafx.geometry.Insets
import javafx.scene.input.MouseEvent
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import tornadofx.*

class Birdseye : View() {

  private var originBefore = svec(0, 0)
  private var mouseBefore = svec(0, 0)

  private var positionYProp = SimpleDoubleProperty(0.0)
  private var positionY by positionYProp

  private val state: State by inject()

  init {
    importStylesheet(CSS_PATH)
    reloadStylesheetsOnFocus()
  }

  override val root = vbox {

    stylesheets += CSS_PATH

    useMaxHeight = true

    lateinit var circle: Circle

    anchorpane {
      styleClass += "my-rect"

      minHeight = 300.0
      maxHeight = 300.0

      minWidth = 300.0
      maxWidth = 300.0

      circle = circle {
        centerXProperty().bind(state.robotXRel)
        centerYProperty().bind(state.robotYRel)
        radius = 10.0
        fill = Color.RED
      }

      addEventFilter(MouseEvent.MOUSE_PRESSED) { e ->
        mouseBefore = svec(e.x, e.y)
        originBefore = svec(state.originX, state.originY)
      }

      addEventFilter(MouseEvent.MOUSE_DRAGGED) { e ->
        val mouseNow = svec(e.x, e.y)
        val diff = mouseNow - mouseBefore

        state.originX = originBefore.x - diff.x
        state.originY = originBefore.y - diff.y

      }
    }

    GlobalScope.launch(Dispatchers.JavaFx) {
      state.dataFlow.collect {
        when (it) {
//          is Data.DriveInput -> TODO()
//          is Data.PathWrapper -> TODO()
//          is Data.PositionInit -> TODO()
          is Data.PurePursuit -> {
            val closest = it.closestPoint.y
            state.robotY = closest
          }
//          is Data.StateChange -> TODO()
//          is Data.TREE -> TODO()
//          is Data.TankInit -> TODO()
          else -> {
          }
        }

      }

    }



    textfield(state.robotYProperty)


  }

}
