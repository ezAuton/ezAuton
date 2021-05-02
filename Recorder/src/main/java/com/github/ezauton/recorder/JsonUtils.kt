package com.github.ezauton.recorder

import com.github.ezauton.conversion.ScalarVector

import java.io.IOException
import com.github.ezauton.core.pathplanning.PathSegment
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.module.SimpleModule
import java.lang.RuntimeException
import com.fasterxml.jackson.databind.util.ISO8601DateFormat
import com.github.ezauton.core.pathplanning.Path
import com.github.ezauton.recorder.serializers.ImmutableVectorSerializer
import com.github.ezauton.recorder.serializers.PathSegmentSerializer
import com.github.ezauton.recorder.serializers.PathSegmentDeserializer
import com.github.ezauton.recorder.serializers.PathSerializer

/**
 * Json utility functions
 *
 * @author Maxwell Harper
 */
object JsonUtils {
  private val objectMapper = ObjectMapper()
  private val customSerializers = SimpleModule()

  /**
   * @return (JSON) String representation of the object.
   */
  @Throws(IOException::class)
  fun toString(o: Any?): String {
    return objectMapper.writeValueAsString(o)
  }

  /**
   * @return (JSON) String representation of the object, or null if an error occurred.
   */
  fun toStringUnchecked(o: Any?): String? {
    return try {
      toString(o)
    } catch (e: IOException) {
      e.printStackTrace()
      null
    }
  }

  /**
   * Turn JSON back into an object
   *
   * @param clazz   A Class<T> representing the type of object the JSON should become (e.g Banana.class)
   * @param jsonStr The JSON data
   * @param <T>     The type of object this data should become (e.g Banana)
   * @return An object of type T containing all the data that the JSON String did
  </T></T> */
  @JvmStatic
  fun <T> toObject(clazz: Class<T>?, jsonStr: String?): T {
    return try {
      objectMapper.readValue(jsonStr, clazz)
    } catch (e: IOException) {
      // TODO: throw a more appropriate unchecked exception here
      throw RuntimeException("problem!!!!", e)
    }
  }

  init {
    // dates should be serialized using ISO pattern
    objectMapper.setDateFormat(ISO8601DateFormat())
    objectMapper.enable(SerializationFeature.INDENT_OUTPUT)
    //        objectMapper.enableDefaultTyping();
    customSerializers.addSerializer(ScalarVector::class.java, ImmutableVectorSerializer())
    customSerializers.addSerializer(PathSegment::class.java, PathSegmentSerializer())
    customSerializers.addDeserializer(PathSegment::class.java, PathSegmentDeserializer())
    customSerializers.addSerializer(Path::class.java, PathSerializer())
    objectMapper.registerModule(customSerializers)
  }
}
