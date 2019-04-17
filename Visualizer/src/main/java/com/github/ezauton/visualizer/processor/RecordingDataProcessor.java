package com.github.ezauton.visualizer.processor;

import com.github.ezauton.recorder.Recording;
import com.github.ezauton.visualizer.processor.factory.DataProcessorFactory;
import com.github.ezauton.visualizer.util.DataProcessor;
import com.github.ezauton.visualizer.util.Environment;
import javafx.animation.Interpolator;
import javafx.animation.KeyValue;

import java.util.*;

public class RecordingDataProcessor implements DataProcessor {

    final List<DataProcessor> childDataProcessors = new ArrayList<>();

    public RecordingDataProcessor(Recording recording, DataProcessorFactory factory) {
        recording.getRecordingMap()
                .values()
                .stream()
                .map(factory::getProcessor)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(childDataProcessors::add);
    }

    @Override
    public void initEnvironment(Environment environment) {
        for (DataProcessor d : childDataProcessors) {
            if (d != null) {
                d.initEnvironment(environment);
            }
        }
    }

    @Override
    public Map<Double, List<KeyValue>> generateKeyValues(Interpolator interpolator) {
        Map<Double, List<KeyValue>> ret = new HashMap<>();
        for (DataProcessor dataProcessor : childDataProcessors) {
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
