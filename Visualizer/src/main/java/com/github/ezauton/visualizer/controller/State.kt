package com.github.ezauton.visualizer.controller

import com.github.ezauton.core.record.Data
import javafx.beans.property.SimpleDoubleProperty
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.internal.ChannelFlow
import kotlinx.coroutines.flow.receiveAsFlow
import tornadofx.Controller
import tornadofx.getValue
import tornadofx.setValue


class State: Controller() {
  val originXProperty = SimpleDoubleProperty(0.0)
  val originYProperty = SimpleDoubleProperty(0.0)

  var originX by originXProperty
  var originY by originYProperty


  private val dataChannel = Channel<Data>(capacity = Channel.UNLIMITED)

  val dataFlow get() = dataChannel.receiveAsFlow()

  suspend fun insertData(data: Flow<Data>){
    data.collect {
      dataChannel.offer(it)
    }
  }
}
