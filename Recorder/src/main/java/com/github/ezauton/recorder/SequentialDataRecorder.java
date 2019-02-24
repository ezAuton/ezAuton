package com.github.ezauton.recorder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.github.ezauton.core.localization.Updateable;
import com.github.ezauton.core.utils.Clock;
import com.github.ezauton.core.utils.Stopwatch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Describes a recorder for data expressed through SequentialDataFrames. Useful for representing motion/robot state over time.
 *
 * @param <T> The type of SequentialDataFrame this DataSequence contains.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public abstract class SequentialDataRecorder<T extends SequentialDataFrame> implements ISubRecording, Updateable, Iterable<T> {
    @JsonIgnore
    private static int sequenceCounter = 0;
    @JsonProperty("dataSequence")
    protected List<T> dataFrames;
    @JsonProperty
    protected String name;
    @JsonIgnore
    protected Stopwatch stopwatch;
    @JsonIgnore
    private int i = 0;

    public SequentialDataRecorder() {
        this("DataSequence_" + sequenceCounter++);
    }

    public SequentialDataRecorder(Clock clock) {
        this("DataSequence_" + sequenceCounter++, clock);
    }

    public SequentialDataRecorder(String name) {
        this.name = name;
        dataFrames = new ArrayList<>();
    }

    public SequentialDataRecorder(String name, Clock clock) {
        this(name);
        this.stopwatch = new Stopwatch(clock);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toJson() {
        return JsonUtils.toStringUnchecked(this);
    }

    public List<T> getDataFrames() {
        return dataFrames;
    }

    public void setDataFrames(List<T> dataFrames) {
        this.dataFrames = dataFrames;
    }

    @Override
    public String toString() {
        return "DataSequence{" +
                "dataFrames=" + dataFrames +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean update() {
        if (i++ == 0) {
            stopwatch.init();
        }
        return checkForNewData();
    }

    @Override
    public Iterator<T> iterator() {
        return dataFrames.iterator();
    }

    /**
     * Process and record new data that has been created since time has elapsed <br>
     * Your constructor should have parameters that allow data sources to be passed in. ]
     *
     * @return If data collection was successful
     */
    public abstract boolean checkForNewData();
}
