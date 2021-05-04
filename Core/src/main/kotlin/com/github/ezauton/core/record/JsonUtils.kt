package com.github.ezauton.core.record

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

private val module = SerializersModule {
//  polymorphic(Data::class) {
//    subclass(PurePursuitData::class)
//    subclass(TREESample::class)
//    subclass(AbstractSample::class)
//  }

}

val format = Json {
  serializersModule = module
  allowStructuredMapKeys = true
}
