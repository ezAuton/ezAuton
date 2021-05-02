package com.github.ezauton.recorder

import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
interface SubRecording {
  val name: String
  fun toJson(): String?
}
