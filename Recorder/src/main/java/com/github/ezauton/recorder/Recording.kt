package com.github.ezauton.recorder

import com.github.ezauton.core.action.SendAction
import com.github.ezauton.core.action.sendAction
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

fun generateRecordingMap(recordings: List<SubRecording>): HashMap<String, SubRecording> {
  val map = HashMap<String, SubRecording>()
  for (recording in recordings) {
    map[recording.name] = recording
  }
  return map
}
@Serializable
class Recording(override val name: String, val recordingMap: Map<String, SubRecording>): SubRecording  {

  @Throws(IOException::class)
  fun save(name: String?) {
    val homeDir = System.getProperty("user.home")
    val filePath = Paths.get(homeDir, ".ezauton", name)
    save(filePath)
    println("saved file!")
  }

  @Throws(IOException::class)
  fun save(filePath: Path) {
    Files.createDirectories(filePath.parent)
    println("path $filePath")
    val writer = Files.newBufferedWriter(filePath)

    val json = format.encodeToString(this)
    writer.write(json)
    writer.close()
  }

  companion object {
    fun from(name: String, recording: List<SubRecording>): Recording {
      return Recording(name, generateRecordingMap(recording))
    }
  }
}

fun groupRecordings(name: String, vararg actions: SendAction<SubRecording>) = sendAction {
  val subRecordings = actions.map { action ->
    async {
      action.first()
    }
  }.awaitAll()

  val recording = Recording.from(name, subRecordings);

  emit(recording)
}
