package com.github.ezauton.visualizer.processor.factory;

import com.github.ezauton.recorder.ISubRecording;
import com.github.ezauton.visualizer.util.IDataProcessor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class FactoryMap implements IDataProcessorFactory {


    private Map<Class<? extends ISubRecording>, Function<ISubRecording, IDataProcessor>> classMap = new HashMap<>();

    @Override
    public Optional<IDataProcessor> getProcessor(ISubRecording subRecording) {
        Function<ISubRecording, IDataProcessor> func = classMap.get(subRecording.getClass());
        if (func == null) return Optional.empty();
        return Optional.ofNullable(func.apply(subRecording));
    }

    public <T extends ISubRecording> void register(Class<T> recordingClass, Function<T, IDataProcessor> function) {

        // JANK AF JAVA WHY YOU HAVE TO BE SO BAD AT GENERICS REEEEEEEEEEEEEEEEEEEEEEEE
        classMap.put(recordingClass, subRecording -> {
            T recording = (T) subRecording;
            return function.apply(recording);
        });
    }
}
