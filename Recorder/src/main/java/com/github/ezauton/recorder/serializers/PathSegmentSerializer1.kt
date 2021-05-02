package com.github.ezauton.recorder.serializers

import com.fasterxml.jackson.databind.ser.std.StdSerializer
import java.io.IOException
import com.fasterxml.jackson.core.JsonGenerationException
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.github.ezauton.core.pathplanning.PathSegment
import java.util.Arrays
import java.lang.Exception
import java.lang.reflect.Field
import java.util.ArrayList

class PathSegmentSerializer @JvmOverloads constructor(t: Class<PathSegment<*>?>? = null) : StdSerializer<PathSegment<*>>(t) {
  @Throws(IOException::class, JsonGenerationException::class)
  override fun serialize(value: PathSegment<*>, jgen: JsonGenerator, provider: SerializerProvider) {
    try {
      jgen.writeStartObject()
      jgen.writeStringField("@class", value.javaClass.name)
      val fields: MutableList<Field> = ArrayList()
      var clazz: Class<*> = value.javaClass
      while (clazz != Any::class.java) {
        fields.addAll(listOf(*clazz.declaredFields))
        clazz = clazz.superclass
      }
      for (field in fields) {
        field.isAccessible = true
        jgen.writeFieldName(field.name)
        jgen.writeObject(field[value])
      }
      jgen.writeEndObject()
    } catch (e: Exception) {
      throw JsonGenerationException("Could not serialize " + value.javaClass.name, e)
    }
  }
}
