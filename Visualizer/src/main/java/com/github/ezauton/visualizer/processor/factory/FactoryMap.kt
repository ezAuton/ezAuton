package com.github.ezauton.visualizer.processor.factory

import com.github.ezauton.recorder.SubRecording
import com.github.ezauton.visualizer.util.DataProcessor
import java.util.*
import java.util.function.Function
import kotlin.reflect.KClass

class FactoryMap : DataProcessorFactory {
  private val classMap: MutableMap<KClass<out SubRecording>, (SubRecording) -> DataProcessor> = HashMap()
  override fun getProcessor(subRecording: SubRecording): DataProcessor? {
    val func = classMap[subRecording::class] ?: return null
    return func(subRecording)
  }

  inline fun <reified T : SubRecording> register(noinline function: (T) -> DataProcessor) {
    registerClass(T::class, function)
  }

  fun <T : SubRecording> registerClass(clazz: KClass<T>, function: (T) -> DataProcessor) {

    // JANK AF JAVA WHY YOU HAVE TO BE SO BAD AT GENERICS REEEEEEEEEEEEEEEEEEEEEEEE
    classMap[clazz] = { subRecording: SubRecording ->
      val recording = subRecording as T
      function(recording)
    }
  }
}
