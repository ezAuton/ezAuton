package com.github.ezauton.core.record

import com.github.ezauton.conversion.Time
import com.github.ezauton.core.action.periodic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class RecordingInternalBuilder {
  fun <T> receiveFlow(flow: Flow<T>) {

  }

}

typealias Sample = Any

interface Sampler {
  fun sample(): Any
}

interface RecordingDSL : CoroutineScope {
  fun sample(period: Time, vararg samplers: Sampler)
}

private class RecordingDSLImpl(val scope: CoroutineScope) : RecordingDSL, CoroutineScope by scope, RecordingBuilder {
  val samples = ArrayList<List<Sample>>()

  override fun sample(period: Time, vararg samplers: Sampler) {
    scope.launch {
      periodic(period) {
        val sampleAtTime = samplers.map { it.sample() }
        samples.add(sampleAtTime)
      }
    }
  }

  override fun build(): Recording {
    TODO("Not yet implemented")
  }


}

interface Recording {

}

interface RecordingBuilder {
  fun build(): Recording
}

fun CoroutineScope.recording(block: RecordingDSL.() -> Unit): RecordingBuilder {
  val impl = RecordingDSLImpl(this)
  impl.block()
  return impl
}
