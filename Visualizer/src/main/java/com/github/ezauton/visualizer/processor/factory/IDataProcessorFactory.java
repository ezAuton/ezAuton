package com.github.ezauton.visualizer.processor.factory;

import com.github.ezauton.recorder.ISubRecording;
import com.github.ezauton.visualizer.util.IDataProcessor;

import java.util.Optional;

public interface IDataProcessorFactory
{
    Optional<IDataProcessor> getProcessor(ISubRecording subRecording);
}
