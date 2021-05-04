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
