package com.team2502.ezauton.recorder;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Describes a sequence of SequentialData frames. Useful for representing motion/robot state over time.
 * @param <T> The type of SequentialDataFrame this DataSequence contains.
 */
public class DataSequence<T extends SequentialDataFrame> implements ISubRecording
{
    @JsonProperty("dataSequence")
    private List<T> dataFrames;

    @JsonProperty
    private String name;

    private static int sequenceCounter = 0;

    public DataSequence() {
        dataFrames = new ArrayList<>();
        name = "DataSequence_" + sequenceCounter++;
    }

    public DataSequence(T... frames)
    {
        name = "DataSequence_" + sequenceCounter++;
        dataFrames = Arrays.asList(frames);
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getJSON()
    {
        return JsonUtils.toStringUnchecked(this);
    }

    public List<T> getDataFrames()
    {
        return dataFrames;
    }

    public void setDataFrames(List<T> dataFrames)
    {
        this.dataFrames = dataFrames;
    }

    @Override
    public String toString()
    {
        return "DataSequence{" +
               "dataFrames=" + dataFrames +
               ", name='" + name + '\'' +
               '}';
    }
}
