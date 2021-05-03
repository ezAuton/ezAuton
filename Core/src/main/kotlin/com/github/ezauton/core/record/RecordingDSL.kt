package com.github.ezauton.core.record

import com.github.ezauton.conversion.Time
import com.github.ezauton.core.action.periodic
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import java.nio.file.Path
import kotlin.io.path.*

interface RecordingInternalBuilder {
  fun <T : Sample> receiveFlow(flow: Flow<T>)
}

interface Sample {
  val key: RecordingKey
}

open class AbstractSample(override val key: RecordingKey): Sample

interface Sampler<T> {
  fun sample(): T
}

interface RecordingDSL : CoroutineScope {
  fun <T : Sample> sample(period: Time, vararg samplers: Sampler<T>)
  fun include(vararg data: Sample)
}


interface RecordingKey

private class RecordingDSLFlowImpl(baseScope: CoroutineScope): RecordingDSL, RecordingInternalBuilder {

  @OptIn(ExperimentalCoroutinesApi::class)
  override val coroutineContext = baseScope.newCoroutineContext(RecordingContext(this))

  private val channel = Channel<Sample>()

  override fun <T : Sample> sample(period: Time, vararg samplers: Sampler<T>) {
    launch {
      periodic(period){
        samplers.asSequence()
          .map { it.sample() }
          .forEach { sample ->
            channel.offer(sample)
          }
      }
    }
  }

  override fun include(vararg data: Sample) {
    data.forEach { channel.offer(it) }
  }

  override fun <T : Sample> receiveFlow(flow: Flow<T>) {
    launch {
      flow.collect {
        channel.offer(it)
      }
    }
  }

  val flow get() = channel.consumeAsFlow()


}

private class RecordingDSLImpl(baseScope: CoroutineScope) : RecordingDSL, CoroutineScope, RecordingBuilder, RecordingInternalBuilder {

  @OptIn(ExperimentalCoroutinesApi::class)
  override val coroutineContext = baseScope.newCoroutineContext(RecordingContext(this))

  private val samples = HashMap<RecordingKey, ArrayList<Sample>>()

  override fun <T : Sample> sample(period: Time, vararg samplers: Sampler<T>) {
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

  override fun include(vararg data: Sample) {
    data.forEach { addSample(it) }
  }

  private fun addSample(sample: Sample) {
    val sampleList = samples.getOrPut(sample.key) { ArrayList() }
    sampleList.add(sample)
  }

  override fun <T : Sample> receiveFlow(flow: Flow<T>) {
    launch {
      flow.collect { sample ->
        addSample(sample)
      }
    }
  }

  override fun build(): Recording = Recording(samples)

}

class Recording internal constructor(val samples: HashMap<RecordingKey, ArrayList<Sample>>){


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

interface RecordingBuilder {
  fun build(): Recording
}

fun CoroutineScope.recording(block: RecordingDSL.() -> Unit): RecordingBuilder {

  val impl = RecordingDSLImpl(this)
  impl.block()
  return impl
}

fun CoroutineScope.recordingFlow(block: RecordingDSL.() -> Unit): Flow<Sample> {
  val impl = RecordingDSLFlowImpl(this)
  impl.block()
  return impl.flow
}
