package com.github.ezauton.visualizer.view

import com.github.ezauton.conversion.svec
import com.github.ezauton.core.record.Data
import com.github.ezauton.visualizer.controller.State
import javafx.beans.property.SimpleDoubleProperty
import javafx.geometry.Insets
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import tornadofx.*

class Birdseye : View() {

  private var originBefore = svec(0, 0)
  private var mouseBefore = svec(0, 0)

  private var positionYProp = SimpleDoubleProperty(0.0)
  private var positionY by positionYProp

  private val state: State by inject()

  override val root = vbox {

    useMaxHeight = true

    padding = Insets(0.0, 0.0, 20.0, 0.0)

    lateinit var circle: Circle

    group {
      rectangle {
        fill = Color.GRAY

        height = 200.0
        width = 200.0

        circle = circle {
          radius = 10.0
          fill = Color.RED
        }

        addEventFilter(MouseEvent.MOUSE_PRESSED) { e ->
          println("mouse pressed")
          mouseBefore = svec(e.x, e.y)
          originBefore = svec(state.originX, state.originY)
        }

        addEventFilter(MouseEvent.MOUSE_DRAGGED) { e ->
          println("mouse dragged")
          val mouseNow = svec(e.x, e.y)
          val diff = mouseNow - mouseBefore

          state.originX = originBefore.x - diff.x
          state.originY = originBefore.y - diff.y

        }
      }

    }

    GlobalScope.launch(Dispatchers.JavaFx){
      state.dataFlow.collect {
        when (it) {
//          is Data.DriveInput -> TODO()
//          is Data.PathWrapper -> TODO()
//          is Data.PositionInit -> TODO()
          is Data.PurePursuit -> {
            circle.centerX = 10.0
            val closest = it.closestPoint.y
            circle.centerY = closest * 10.0
          }
//          is Data.StateChange -> TODO()
//          is Data.TREE -> TODO()
//          is Data.TankInit -> TODO()
          else -> {
          }
        }

      }

    }



    textfield(circle.centerYProperty())



  }

}
