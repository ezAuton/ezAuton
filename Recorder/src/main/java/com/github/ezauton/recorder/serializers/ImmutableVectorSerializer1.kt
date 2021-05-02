package com.github.ezauton.recorder.serializers

import com.github.ezauton.conversion.ScalarVector
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import java.io.IOException
import com.fasterxml.jackson.core.JsonGenerationException
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import java.lang.Exception

class ImmutableVectorSerializer @JvmOverloads constructor(t: Class<ScalarVector?>? = null) : StdSerializer<ScalarVector>(t) {
  @Throws(IOException::class, JsonGenerationException::class)
  override fun serialize(value: ScalarVector, jgen: JsonGenerator, provider: SerializerProvider) {
    try {
      jgen.writeStartObject()
      jgen.writeFieldName("elements")
      jgen.writeStartArray()
      value.stream().forEach { n: Double ->
        try {
          jgen.writeNumber(n)
        } catch (e: IOException) {
          e.printStackTrace() // TODO something more meaningful?
        }
      }
      jgen.writeEndArray()
      jgen.writeEndObject()
    } catch (e: Exception) {
      throw JsonGenerationException("Could not serialize ScalarVector", e)
    }
  }
}
