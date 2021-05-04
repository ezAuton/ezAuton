package com.github.ezauton.visualizer.view

import com.github.ezauton.conversion.seconds
import com.github.ezauton.conversion.svec
import com.github.ezauton.core.record.Data
import com.github.ezauton.visualizer.controller.State
import javafx.beans.property.SimpleDoubleProperty
import javafx.geometry.Insets
import javafx.scene.input.MouseEvent
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

  private val controller: State by inject()

  override val root = vbox {

    useMaxHeight = true

    padding = Insets(0.0, 0.0, 20.0, 0.0)



    GlobalScope.launch(Dispatchers.JavaFx){
      controller.dataFlow.onEach { delay(4) } .collect {
        when (it) {
//          is Data.DriveInput -> TODO()
//          is Data.PathWrapper -> TODO()
//          is Data.PositionInit -> TODO()
          is Data.PurePursuit -> {
            positionY = it.closestPoint.y
          }
//          is Data.StateChange -> TODO()
//          is Data.TREE -> TODO()
//          is Data.TankInit -> TODO()
          else -> {
          }
        }

      }

    }



    textfield(positionYProp)


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
