package com.github.ezauton.core.record

import com.github.ezauton.conversion.Time
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class RecordingInternalBuilder {
  fun <T> receiveFlow(flow: Flow<T>){

  }

}

interface Sample {

}


interface RecordingDSL: CoroutineScope {
  fun sample(period: Time, vararg samples: Any)
}

interface Recording {

}

interface RecordingBuilder {
  fun build(): Recording
}

fun recording(block: RecordingDSL.() -> Unit): RecordingBuilder{

}
