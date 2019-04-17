package com.github.ezauton.visualizer.processor.factory;

import com.github.ezauton.recorder.SubRecording;
import com.github.ezauton.visualizer.util.DataProcessor;

import java.util.Optional;

public interface DataProcessorFactory {
    Optional<DataProcessor> getProcessor(SubRecording subRecording);
}
