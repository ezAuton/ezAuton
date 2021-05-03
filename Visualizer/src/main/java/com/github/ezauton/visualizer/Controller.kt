package com.github.ezauton.visualizer

import com.github.ezauton.conversion.ScalarVector
import com.github.ezauton.conversion.svec
import com.github.ezauton.core.utils.math.epsilonEquals
import com.github.ezauton.recorder.Recording
import com.github.ezauton.recorder.format
import com.github.ezauton.visualizer.processor.factory.FactoryMap
import com.github.ezauton.visualizer.util.DataProcessor
import com.github.ezauton.visualizer.util.Environment
import javafx.animation.*
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.event.Event
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.GridPane
import javafx.stage.FileChooser
import javafx.util.Duration
import kotlinx.serialization.decodeFromString
import java.io.File
import java.io.IOException
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.function.Consumer

internal enum class StartPos(val proportionX: Double, private val proportionY: Double) {
  LEFT_HAB2(163 / 443.0, 485 / 492.0), LEFT_HAB1(163 / 443.0, 421 / 492.0), CENTER(.5, 421 / 492.0), RIGHT_HAB2(1 - 163.0 / 443.0, 485 / 492.0), RIGHT_HAB1(1 - 163.0 / 443.0, 421 / 492.0);

  fun getProportionY(): Double {
    return 1 - proportionY
  }

  override fun toString(): String {
    return name
  }
}

class Controller : Initializable {

  @FXML
  lateinit var btnSelectJsonLogFile: Button
  @FXML
  lateinit var backdrop: AnchorPane
  @FXML
  private lateinit var timeSlider: Slider
  @FXML
  private lateinit var tabPane: TabPane
  @FXML
  private lateinit var btnSkipToStart: Button
  @FXML
  private lateinit var btnSkipToEnd: Button
  @FXML
  private lateinit var btnAdvanceOneFrame: Button
  @FXML
  private lateinit var btnRewindOneFrame: Button
  @FXML
  private lateinit var btnPlayPause: Button
  @FXML
  private lateinit var rateSlider: Slider
  @FXML
  private lateinit var timeElapsed: Label
  @FXML
  private lateinit var posChooser: ChoiceBox<StartPos>
  @FXML
  private lateinit var clickedCoordsDisplay: Label

  /**
   * The coordinate (0, 0) is in the top left corner of the screen. Since driving forwards = up, this is bad.
   * The coordinate pair (originX, originY) dictates the absolute starting position of everything.
   *
   *
   * This way, we can make originY non zero allowing us to actually see the path
   */
  private var originX = -1234.0
  private var originY = -1234.0

  /**
   * By default, the robot is 2-3 pixels tall. This is much too small to learn anything.
   * This scales everything up so that everything is still proportional to each other but you can at least see it
   */
  private var spatialScaleFactor = 0.0


  private var timeline: Timeline? = null
  private var rateSliderListener: ChangeListener<Number>? = null
  private lateinit var currentRecording: Recording

  override fun initialize(location: URL, resources: ResourceBundle?) {

    backdrop.heightProperty().addListener { heightProp: ObservableValue<out Number>?, oldHeight: Number?, newHeight: Number ->  spatialScaleFactor = newHeight.toDouble() / 30.0156 }
    backdrop.widthProperty().addListener { widthProp: ObservableValue<out Number>?, oldWidth: Number?, newWidth: Number ->
      try {
        originX = posChooser.value.proportionX * newWidth.toDouble()
      } catch (e: NullPointerException) {
        // this is ok
      }
    }


    // Make sure the circle always stays with the robot
    backdrop.onMouseClicked = EventHandler { e: MouseEvent -> displayRealWorldCoordsOnClick(e) }
    val listOfCSVs = getAllFilesInDirectory(Paths.get(System.getProperty("user.home"), ".ezauton").toString())
    posChooser.items.addAll(*StartPos.values())

    posChooser.valueProperty().addListener { selectedProp: ObservableValue<out StartPos?>?, oldSelected: StartPos?, newSelected: StartPos? ->
      originX = posChooser.value.proportionX * backdrop.width
      originY = backdrop.height - posChooser.value.getProportionY() * backdrop.height
      animateSquareKeyframe(null)
    }

  }

  /**
   * Clear the tab pane to the right and the canvas with the field on it
   */
  private fun clear() {
    tabPane.tabs.clear()
    backdrop.children.clear()
  }

  /**
   * Animate the robot following the path
   *
   * @param event This exists in case you want to add this as an onClickListener or something like that. Not used.
   */
  @FXML
  private fun animateSquareKeyframe(event: Event?) {
    // must have a file and position
    if (posChooser.value == null) {
      System.err.println("Please select a file and a position")
      return
    }

    // Animation works by interpolating key values between key frames
    // We store all our keyframes in this handy dandy list
    val keyFrames: MutableList<KeyFrame> = ArrayList<KeyFrame>()
    val interpolator: Interpolator = Interpolator.DISCRETE

    // Clear everything
    clear()
    for ((key) in currentRecording.recordingMap) {
      // Add new tab for each sub-recording
      val content = GridPane()
      content.alignment = Pos.CENTER
      tabPane.tabs.add(Tab(key, content))
    }

    // Initialize data processors and whatnot
    val factory: FactoryMap = Visualizer.instance.factory

    // currentRecorder... holds values of RECORDINGS... maps to RECORDING PROCESSORS
    val dataProcessor: DataProcessor = factory.getProcessor(currentRecording) ?: throw IllegalStateException("could not find current recording")
    val env = environment
    dataProcessor.initEnvironment(env)
    val keyValues = dataProcessor.generateKeyValues(interpolator).entries.sortedBy {
      it.key
    }

    val keyValItr = keyValues.iterator()

    // Add first keyframe
    var keyValList = keyValItr.next().value + KeyValue(timeElapsed.textProperty(), "0 seconds")
    var keyValArray = keyValList.toTypedArray()

    keyFrames.add(
      KeyFrame(
        Duration.ZERO,
        *keyValArray
      )
    )

    while (keyValItr.hasNext()) {
      val (key, value) = keyValItr.next()
      keyValList = value
      keyValList = keyValList + KeyValue(timeElapsed.textProperty(), String.format("%.02f seconds", key / 1000))
      keyValArray = keyValList.toTypedArray()
      keyFrames.add(
        KeyFrame(
          Duration.millis(key),
          *keyValArray
        )
      )
    }

    // Create the animation
    if (rateSliderListener != null) {
      rateSlider.valueProperty().removeListener(rateSliderListener)
    }

    timeline?.pause()

    val timeline = Timeline()
    this.timeline = Timeline()


    // Loop it forever
    timeline.cycleCount = Timeline.INDEFINITE

    // When the animation ends, the robot teleports from the end to the beginning instead of driving backwards
    timeline.isAutoReverse = false

    // Add our keyframes to the animation
    keyFrames.forEach { kf -> timeline.keyFrames.add(kf) }

    /// Begin binds
    rateSliderListener = ChangeListener { observable: ObservableValue<out Number>?, oldValue: Number?, newValue: Number ->
      val value = newValue.toDouble()
      actOnTimeline(timeline, value)
    }

    rateSlider.valueProperty().addListener(rateSliderListener)
    timeSlider.min = 0.0
    timeSlider.max = timeline.getCycleDuration().toSeconds()
    timeSlider.majorTickUnit = 1.0
    val wasPlaying = AtomicBoolean(true)
    val updateTimelineTimeListener = ChangeListener { observable: ObservableValue<out Number?>?, oldValue: Number?, newValue: Number? -> timeline.jumpTo(Duration.seconds(timeSlider.getValue())) }
    val updateSliderPositionListener = ChangeListener { prop: ObservableValue<out Duration>?, old: Duration?, newVal: Duration -> timeSlider.setValue(newVal.toSeconds()) }
    timeline.currentTimeProperty().addListener(updateSliderPositionListener)

    timeSlider.onMousePressed = EventHandler { e: MouseEvent? ->
      wasPlaying.set(timeline.status == Animation.Status.RUNNING)
      pause()
      timeline.currentTimeProperty().removeListener(updateSliderPositionListener)
      timeSlider.valueProperty().addListener(updateTimelineTimeListener)
    }

    timeSlider.onMouseReleased = EventHandler { e: MouseEvent? ->
      if (wasPlaying.get()) {
        play()
      }
      timeSlider.valueProperty().removeListener(updateTimelineTimeListener)
      timeline.currentTimeProperty().addListener(updateSliderPositionListener)
    }
    btnPlayPause.onMouseClicked = EventHandler { e: MouseEvent? ->
      if (timeline.status == Animation.Status.RUNNING) {
        pause()
      } else {
        play()
      }
    }
    btnAdvanceOneFrame.onMouseClicked = EventHandler { e: MouseEvent? ->
      pause()
      timeline.jumpTo(timeline.currentTime.add(Duration(1000 / timeline.targetFramerate)))
    }
    btnRewindOneFrame.onMouseClicked = EventHandler { e: MouseEvent? ->
      pause()
      timeline.jumpTo(timeline.currentTime.subtract(Duration(1000 / timeline.targetFramerate)))
    }
    btnSkipToStart.onMouseClicked = EventHandler { e: MouseEvent? ->
      pause()
      timeline.jumpTo(Duration(3000 / timeline.targetFramerate)) // skip 3 frames so that stuff will be in the right spot.
    }
    btnSkipToEnd.onMouseClicked = EventHandler { e: MouseEvent? ->
      pause()
      timeline.jumpTo(timeline.cycleDuration)
    }

    // End binds
    // Play it
    timeline.playFromStart()
    btnPlayPause.text = "Pause"
    actOnTimeline(timeline, rateSlider.valueProperty().doubleValue())
  }

  private fun pause() {
    timeline?.pause()
    btnPlayPause.text = "Play"
  }

  private fun play() {
    if (timeline?.status == Animation.Status.STOPPED) {
      timeline?.playFromStart()
      btnPlayPause.text = "Pause"
    } else {
      timeline?.play()
      btnPlayPause.text = "Pause"
    }
  }

  private val environment: Environment
    get() = object : Environment {

      override val fieldAnchorPane: AnchorPane get() = backdrop

      override fun getDataGridPane(name: String): GridPane {
        for (tab in tabPane.tabs) {
          if (tab.text == name && tab.content is GridPane) {
            return tab.content as GridPane
          }
        }
        throw NullPointerException("Cannot find tab with name: $name")
      }

      override val scaleFactorX: Double get() = spatialScaleFactor
      override val scaleFactorY: Double get() = spatialScaleFactor

      override val origin: ScalarVector
        get() = svec(originX, originY)
    }

  /**
   * Handles pausing/playing the timeline based on the rate slider
   */
  private fun actOnTimeline(timeline: Timeline, value: Double) {
    if (0.0.epsilonEquals(value)) {
      timeline.pause()
    } else {
      play()
      timeline.rate = value
    }
  }

  @FXML
  private fun displayRealWorldCoordsOnClick(e: MouseEvent) {
    val xFt = (e.x - originX) / spatialScaleFactor
    val yFt = (originY - e.y) / spatialScaleFactor
    if (originX.epsilonEquals(-1234.0) && originY.epsilonEquals(-1234.0)) {
      clickedCoordsDisplay.text = "Select a starting position first."
    } else {
      clickedCoordsDisplay.text = String.format("(%f, %f)", xFt, yFt)
    }
  }

  @FXML
  private fun selectFile(e: Event) {
    e.consume()
    val fileChooser = FileChooser()
    fileChooser.title = "Select JSON Recording"
    fileChooser.selectedExtensionFilter = FileChooser.ExtensionFilter("JSON file", "*.json")
    try {
      val jsonFile: File = fileChooser.showOpenDialog(Visualizer.instance.stage)
      loadRecording(jsonFile)
      btnSelectJsonLogFile.text = jsonFile.name
    } catch (err: Exception) {
      err.printStackTrace()
      val alert = Alert(Alert.AlertType.ERROR)
      alert.title = "Error deserializing log file"
      alert.contentText = "Are you sure that you picked a json recording? \n See stacktrace in console."
      alert.showAndWait()
    }
  }

  /**
   * Loads a recording from a .json file
   *
   * @param jsonFile
   * @throws IOException If the file cannot be read from
   */
  @Throws(IOException::class)
  private fun loadRecording(jsonFile: File) {
    val lines = Files.readAllLines(jsonFile.toPath())
    val fileContentsSb = StringBuilder()
    lines.forEach(Consumer { str: String? -> fileContentsSb.append(str) })
    val json = fileContentsSb.toString()
    currentRecording = format.decodeFromString(json)
    animateSquareKeyframe(null)
  }

  companion object {
    private fun getExtension(fileName: String): String {
      //        System.out.println("ret = " + ret);
      return fileName.substring(fileName.lastIndexOf(".") + 1)
    }

    private fun getAllFilesInDirectory(dir: String): List<File> {
      val files: MutableList<File> = ArrayList()
      val folder = File(dir)
      val listOfFiles = folder.listFiles()

      if(listOfFiles == null ){
        folder.mkdir()
        return emptyList()
      }

      return try {
        for (i in listOfFiles.indices) {
          if (listOfFiles[i].isFile && getExtension(listOfFiles[i].name).equals("json", ignoreCase = true)) {
            files.add(listOfFiles[i])
          }
        }
        files
      } catch (e: NullPointerException) {
        folder.mkdir()
        ArrayList()
      }
    }
  }

  /**
   *
   */
  init {
    // Read the config file in the resources folder and initialize values appropriately
    val homeDir = System.getProperty("user.home")
    val filePath: Path = Paths.get(homeDir, ".ezauton", "config", "visualizer.config")
    try {
      Files.createDirectories(filePath.parent)
    } catch (e: IOException) {
      e.printStackTrace()
    }
    try {
      if (!Files.exists(filePath)) {
        Files.createFile(filePath)
      }
    } catch (e: IOException) {
      e.printStackTrace()
    }
  }
}
