package com.github.ezauton.visualizer.controller

import javafx.beans.property.SimpleDoubleProperty
import tornadofx.Controller
import tornadofx.getValue
import tornadofx.setValue


class State: Controller() {
  val originXProperty = SimpleDoubleProperty(0.0)
  val originYProperty = SimpleDoubleProperty(0.0)

  var originX by originXProperty
  var originY by originYProperty
}
