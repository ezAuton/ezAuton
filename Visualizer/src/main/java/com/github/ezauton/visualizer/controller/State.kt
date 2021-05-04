package com.github.ezauton.visualizer.controller

import com.github.ezauton.core.record.Data
import javafx.beans.property.SimpleDoubleProperty
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import tornadofx.Controller


@OptIn(ExperimentalCoroutinesApi::class)
class State : Controller() {

  val robotXRel = SimpleDoubleProperty(0.0)
  val robotYRel = SimpleDoubleProperty(0.0)
//  val originXProperty = SimpleDoubleProperty(0.0)
//  val originYProperty = SimpleDoubleProperty(0.0)


  var robotX = 0.0
    set(value) {
      robotXRel.set(value - originX)
      field = value
    }


  var originX = 0.0
    set(value) {
      robotXRel.set(robotX - value)
      field = value
    }

  val robotYProperty = SimpleDoubleProperty()

  var robotY = 0.0
    set(value) {
      robotYProperty.set(value)
      robotYRel.set(value - originY)
      field = value
    }


  var originY = 0.0
    set(value) {
      robotYRel.set(robotY - value)
      field = value
    }

  private val dataChannel = Channel<Data>(capacity = Channel.UNLIMITED)

  val dataFlow get() = dataChannel.receiveAsFlow()

  suspend fun insertData(data: Flow<Data>) {
    data.collect {
      dataChannel.offer(it)
    }
  }
}
