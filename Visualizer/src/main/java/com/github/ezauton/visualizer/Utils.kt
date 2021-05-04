package com.github.ezauton.visualizer

import javafx.scene.image.Image
import javafx.stage.FileChooser
import tornadofx.FileChooserMode
import tornadofx.chooseFile
import java.io.InputStream
import java.net.URL


class Utils

fun resource(name: String): URL? {
  return Utils::class.java.getResource(name)
}

fun resourceStream(name: String): InputStream? {
  return Utils::class.java.getResourceAsStream(name)
}

val MAIN_ICON by lazy {
  val resource = requireNotNull(resourceStream("icon.png"))
  Image(resource)
}

val JSON_FILTER by lazy {
  val extFilter = FileChooser.ExtensionFilter("JSON files (*.json)", "*.json")
  arrayOf(extFilter)
}

val CSS_PATH by lazy {
  val cssResource = requireNotNull(resource("application.css"))
  cssResource.toExternalForm()
}

fun chooseJsonFile() = chooseFile("Choose a JSON file", filters = JSON_FILTER, mode = FileChooserMode.Single).firstOrNull()?.toPath()
