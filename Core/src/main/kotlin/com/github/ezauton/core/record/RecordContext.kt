package com.github.ezauton.core.record

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

class RecordingContext(val recording: RecordingDSL) : AbstractCoroutineContextElement(RecordingContext) {
  companion object Key : CoroutineContext.Key<RecordingContext>
}
