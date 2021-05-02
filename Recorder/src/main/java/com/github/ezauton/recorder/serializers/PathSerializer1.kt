package com.github.ezauton.recorder.serializers

import com.fasterxml.jackson.databind.ser.std.StdSerializer
import java.io.IOException
import com.fasterxml.jackson.core.JsonGenerationException
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.github.ezauton.core.pathplanning.Path
import java.lang.Exception

class PathSerializer @JvmOverloads constructor(t: Class<Path<*>?>? = null) : StdSerializer<Path<*>>(t) {
  @Throws(IOException::class, JsonGenerationException::class)
  override fun serialize(value: Path<*>, jgen: JsonGenerator, provider: SerializerProvider) {
    try {
      val pathSegments = value.pathSegments
      val length = value.distance.value
      jgen.writeStartObject()
      jgen.writeFieldName("pathSegments")
      jgen.writeObject(pathSegments)
      jgen.writeFieldName("length")
      jgen.writeNumber(length)
      jgen.writeEndObject()
    } catch (e: Exception) {
      throw JsonGenerationException("Could not serialize Path", e)
    }
  }
}
