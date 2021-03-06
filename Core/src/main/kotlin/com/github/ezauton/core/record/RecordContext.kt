package com.github.ezauton.core.record

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

class RecordingContext(val recording: RecordingInternalBuilder) : AbstractCoroutineContextElement(RecordingContext) {
  companion object Key : CoroutineContext.Key<RecordingContext>
}
