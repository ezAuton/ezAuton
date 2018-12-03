package org.github.ezauton.ezauton.recorder;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.github.ezauton.ezauton.visualizer.IDataProcessor;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public interface ISubRecording
{
    String getName();

    String toJson();

    IDataProcessor createDataProcessor();
}
