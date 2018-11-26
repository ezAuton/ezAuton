package org.github.ezauton.ezauton.recorder;

import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import org.github.ezauton.ezauton.localization.Updateable;
import org.github.ezauton.ezauton.visualizer.IDataProcessor;
import org.github.ezauton.ezauton.visualizer.IEnvironment;

import java.util.*;

public class Recording implements ISubRecording, Updateable
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

    @Override
    public IDataProcessor createDataProcessor()
    {
        final List<IDataProcessor> childDataProcessors = new ArrayList<>();
        recordingMap.values().forEach(r -> childDataProcessors.add(r.createDataProcessor()));

        return new IDataProcessor()
        {
            @Override
            public void initEnvironment(IEnvironment environment)
            {
                for(IDataProcessor d : childDataProcessors)
                {
                    if(d != null)
                    {
                        d.initEnvironment(environment);
                    }
                }
            }

            @Override
            public Map<Double, List<KeyValue>> forKeyFrame(Interpolator interpolator)
            {
                Map<Double, List<KeyValue>> ret = new HashMap<>();
                for(IDataProcessor dataProcessor : childDataProcessors)
                {
                    if(dataProcessor != null)
                    {
                        Map<Double, List<KeyValue>> keyValMap = dataProcessor.forKeyFrame(interpolator);
                        if(keyValMap != null)
                        {
                            for(Map.Entry<Double, List<KeyValue>> entry : keyValMap.entrySet())
                            {
                                if(!ret.containsKey(entry.getKey())) // not contained
                                {
                                    ret.put(entry.getKey(), new ArrayList<>(entry.getValue()));
                                }
                                else
                                { // contained
                                    ret.get(entry.getKey()).addAll(entry.getValue());
                                }
                            }
                        }
                    }
                }
                return ret;
            }
        };
    }

    public Map<String, ISubRecording> getRecordingMap()
    {
        return recordingMap;
    }

    @Override
    public boolean update()
    {
        boolean ret = false;
        for(ISubRecording recording : recordingMap.values())
        {
            if(recording instanceof Updateable)
            {
                // If the update method returns true
                if(((Updateable) recording).update())
                {
                    ret = true;
                }
            }
        }
        return ret;
    }
}
