package com.github.ezauton.recorder

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.ezauton.core.action.SendAction
import com.github.ezauton.core.action.action
import com.github.ezauton.core.action.sendAction
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class Recording(@JsonProperty("name") override val name: String, recordings: List<SubRecording>) : SubRecording {

  @JsonProperty("recordingData")
  val recordingMap: Map<String, SubRecording> = run {
    val map = HashMap<String, SubRecording>()
    for (recording in recordings) {
      map[recording.name] = recording
    }
    map
  }

  override fun toJson(): String? {
    return JsonUtils.toStringUnchecked(this)
  }

  @Throws(IOException::class)
  fun save(name: String?) {
    val homeDir = System.getProperty("user.home")
    val filePath = Paths.get(homeDir, ".ezauton", name)
    save(filePath)
  }

  @Throws(IOException::class)
  fun save(filePath: Path) {
    Files.createDirectories(filePath.parent)
    val writer = Files.newBufferedWriter(filePath)
    val json = toJson() ?: throw IllegalStateException("could not convert to json");
    writer.write(json)
    writer.close()
    JsonUtils.toObject(Recording::class.java, json)
  }
}

fun groupRecordings(name: String, vararg actions: SendAction<SubRecording>) = sendAction {
  val subRecordings = actions.map { action ->
    async {
      action.first()
    }
  }.awaitAll()

  val recording = Recording(name, subRecordings);

  emit(recording)
}
