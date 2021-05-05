package com.github.ezauton.core.record

import com.github.ezauton.conversion.Time
import com.github.ezauton.core.action.AbortActionException
import com.github.ezauton.core.action.periodic
import com.github.ezauton.core.utils.Stopwatch
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.*


interface RecordingInternalBuilder {
  fun <T : Data> receiveFlow(flow: Flow<T>)
}

interface Sampler<T> {
  fun sample(): T
}

interface RecordingDSL : CoroutineScope {
  fun <T : Data> sample(period: Time, vararg samplers: Sampler<T>)
  fun include(vararg data: Data)
}


private class RecordingDSLFlowImpl(baseScope: CoroutineScope) : RecordingDSL, RecordingInternalBuilder {

  @OptIn(ExperimentalCoroutinesApi::class)
  override val coroutineContext = baseScope.newCoroutineContext(RecordingContext(this))

  private val stopwatch = Stopwatch.start()

  private val channel = Channel<Packet>()

  override fun <T : Data> sample(period: Time, vararg samplers: Sampler<T>) {
    launch {
      periodic(period) {
        samplers.asSequence()
          .map { it.sample() }
          .forEach { sample ->
            add(sample)
          }
      }
    }
  }

  private fun add(sample: Data) {
    val packet = Packet(sample, stopwatch.read())
    channel.offer(packet)
  }

  override fun include(vararg data: Data) {
    data.forEach(::add)
  }

  override fun <T : Data> receiveFlow(flow: Flow<T>) {
    launch {
      flow.collect(::add)
    }
  }

  val flow get() = channel.consumeAsFlow()


}

private class RecordingDSLImpl(baseScope: CoroutineScope) : RecordingDSL, CoroutineScope, RecordingBuilder, RecordingInternalBuilder {

  @OptIn(ExperimentalCoroutinesApi::class)
  override val coroutineContext = baseScope.newCoroutineContext(RecordingContext(this))

  private val data = ArrayDeque<Packet>()

  val stopwatch = Stopwatch.start()

  override fun <T : Data> sample(period: Time, vararg samplers: Sampler<T>) {
    launch {
      periodic(period) {
        samplers.asSequence()
          .map { it.sample() }
          .forEach { sample ->
            addSample(sample)
          }
      }
    }
  }


  override fun include(vararg data: Data) {
    data.forEach { addSample(it) }
  }

  private fun addSample(sample: Data) {
    val packet = Packet(sample, stopwatch.read())
    data.add(packet)
  }

  override fun <T : Data> receiveFlow(flow: Flow<T>) {
    launch {
      flow.collect { sample ->
        addSample(sample)
      }
    }
  }

  override fun build(): Recording = Recording(data)

}

@Serializable
class Recording internal constructor(val samples: List<Packet>) {

  @OptIn(ExperimentalPathApi::class)
  fun save(path: Path) {
    val json = format.encodeToString(this)

    path.deleteIfExists()
    path.createFile()

    path.bufferedWriter().use { writer ->
      writer.write(json)
    }

  }


  companion object {
    @OptIn(ExperimentalPathApi::class)
    fun load(path: Path): Recording = path.bufferedReader().use { reader ->
      val json = reader.readText()
      format.decodeFromString(json)
    }
  }
}

fun List<Data>.delayProperly() {

}

fun Recording.save(name: String) {
  val homeDir = System.getProperty("user.home")
  val filePath = Paths.get(homeDir, ".ezauton", name)
  save(filePath)
}

interface RecordingBuilder {
  fun build(): Recording
}

suspend fun recording(block: RecordingDSL.() -> Unit): Recording {
  var builder: RecordingBuilder? = null
  try {
     coroutineScope {
      val impl = RecordingDSLImpl(this)
      builder = impl
      impl.block()
    }
  } catch (e: AbortActionException){
    // do nothing
  }
  return builder?.build()!!
}

fun CoroutineScope.recordingFlow(block: RecordingDSL.() -> Unit): Flow<Packet> {
  val impl = RecordingDSLFlowImpl(this)
  impl.block()
  return impl.flow
}

fun List<Packet>.realisticFlow(): Flow<Data> {
  val stopwatch = Stopwatch.start()
  return asFlow().map {
    val dTime = it.sentTime - stopwatch.read()
    if (dTime.isPositive) delay(dTime.millisL)
    it.data
  }
}
