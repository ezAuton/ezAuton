package com.team2502.ezauton.recorder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Recording implements ISubRecording
{
    @JsonProperty("recordingData")
    private Map<String, ISubRecording> recordingMap = new HashMap<>();

    @JsonProperty("name")
    private String name;

    private static int recordingCounter = 0;

    public Recording()
    {
        name = "Recording_" + recordingCounter++;
    }

    public Recording(ISubRecording... recordings)
    {
        this("Recording_" + recordingCounter++, recordings);
    }

    public Recording(String name, ISubRecording... recordings)
    {
        this.name = name;
        for(ISubRecording recording : recordings)
        {
            recordingMap.put(recording.getName(), recording);
        }
    }

    public void addSubRecording(ISubRecording subRecording)
    {
        Optional.ofNullable(subRecording).ifPresent(r -> recordingMap.put(r.getName(), r));
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String toJson()
    {
        return JsonUtils.toStringUnchecked(this);
    }
}
