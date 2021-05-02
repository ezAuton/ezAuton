package com.github.ezauton.recorder.serializers

import java.io.IOException
import com.github.ezauton.core.pathplanning.PathSegment
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.node.ObjectNode
import java.lang.Exception

class PathSegmentDeserializer @JvmOverloads constructor(t: Class<PathSegment<*>?>? = null) : StdDeserializer<PathSegment<*>>(t) {
  @Throws(IOException::class, JsonProcessingException::class)
  override fun deserialize(jp: JsonParser, ctxt: DeserializationContext): PathSegment<*> {

    val mp = ObjectMapper()
    mp.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    val node = jp.codec.readTree<ObjectNode>(jp)
    return try {
      val clazz = Class.forName(node["@class"].textValue()) as Class<PathSegment<*>>
      node.remove("@class")
      mp.readValue(node.toString(), clazz)
    } catch (e: Exception) {
      System.err.println(node.toString())
      throw IOException("Cannot deserialize alleged PathSegment", e)
    }
  }
}
