package com.github.ezauton.recorder;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public interface ISubRecording
{
    String getName();

    String toJson();

//    IDataProcessor createDataProcessor();
}
