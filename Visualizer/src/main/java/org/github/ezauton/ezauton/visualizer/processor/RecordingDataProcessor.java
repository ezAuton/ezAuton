package org.github.ezauton.ezauton.visualizer.processor;

import javafx.animation.Interpolator;
import javafx.animation.KeyValue;
import org.github.ezauton.ezauton.recorder.Recording;
import org.github.ezauton.ezauton.visualizer.IDataProcessor;
import org.github.ezauton.ezauton.visualizer.IEnvironment;
import org.github.ezauton.ezauton.visualizer.processor.factory.IDataProcessorFactory;

import java.util.*;

public class RecordingDataProcessor implements IDataProcessor
{

    final List<IDataProcessor> childDataProcessors = new ArrayList<>();

    public RecordingDataProcessor(Recording recording, IDataProcessorFactory factory)
    {
        recording.getRecordingMap()
                .values()
                .stream()
                .map(factory::getProcessor)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(childDataProcessors::add);
    }

    @Override
    public void initEnvironment(IEnvironment environment)
    {
        for (IDataProcessor d : childDataProcessors) {
            if (d != null) {
                d.initEnvironment(environment);
            }
        }
    }

    @Override
    public Map<Double, List<KeyValue>> generateKeyValues(Interpolator interpolator)
    {
        Map<Double, List<KeyValue>> ret = new HashMap<>();
        for (IDataProcessor dataProcessor : childDataProcessors) {
            if (dataProcessor != null) {
                Map<Double, List<KeyValue>> keyValMap = dataProcessor.generateKeyValues(interpolator);
                if (keyValMap != null) {
                    for (Map.Entry<Double, List<KeyValue>> entry : keyValMap.entrySet()) {
                        if (!ret.containsKey(entry.getKey())) // not contained
                        {
                            ret.put(entry.getKey(), new ArrayList<>(entry.getValue()));
                        } else { // contained
                            ret.get(entry.getKey()).addAll(entry.getValue());
                        }
                    }
                }
            }
        }
        return ret;
    }
}
