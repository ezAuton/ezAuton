package com.github.ezauton.visualizer.processor.factory;

import com.github.ezauton.recorder.SubRecording;
import com.github.ezauton.visualizer.util.DataProcessor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class FactoryMap implements DataProcessorFactory {


    private Map<Class<? extends SubRecording>, Function<SubRecording, DataProcessor>> classMap = new HashMap<>();

    @Override
    public Optional<DataProcessor> getProcessor(SubRecording subRecording) {
        Function<SubRecording, DataProcessor> func = classMap.get(subRecording.getClass());
        if (func == null) return Optional.empty();
        return Optional.ofNullable(func.apply(subRecording));
    }

    public <T extends SubRecording> void register(Class<T> recordingClass, Function<T, DataProcessor> function) {

        // JANK AF JAVA WHY YOU HAVE TO BE SO BAD AT GENERICS REEEEEEEEEEEEEEEEEEEEEEEE
        classMap.put(recordingClass, subRecording -> {
            T recording = (T) subRecording;
            return function.apply(recording);
        });
    }
}
