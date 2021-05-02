package com.github.ezauton.recorder

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeInfo

/**
 * Describes a recorder for data expressed through SequentialDataFrames. Useful for representing motion/robot state over time.
 *
 * @param <T> The type of SequentialDataFrame this DataSequence contains.
</T> */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")

class SeqRecording<T : SequentialDataFrame>(@JsonProperty override val name: String, @JsonProperty("dataSequence") val dataFrames: List<T>) : SubRecording, Iterable<T> {


  override fun toJson(): String? {
    return JsonUtils.toStringUnchecked(this)
  }

  override fun toString(): String {
    return "DataSequence{" +
        "dataFrames=" + dataFrames +
        ", name='" + name + '\'' +
        '}'
  }

  override fun iterator(): Iterator<T> {
    return dataFrames.iterator()
  }

  companion object {
    @JsonIgnore
    private var sequenceCounter = 0

    fun <T : SequentialDataFrame> of(name: String, dataFrames: List<T>): SeqRecording<T> {
      return SeqRecording(name, dataFrames)
    }

  }



}
