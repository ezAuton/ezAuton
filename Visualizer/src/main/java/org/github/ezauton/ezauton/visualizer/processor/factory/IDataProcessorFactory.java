package org.github.ezauton.ezauton.visualizer.processor.factory;

import org.github.ezauton.ezauton.recorder.ISubRecording;
import org.github.ezauton.ezauton.visualizer.util.IDataProcessor;

import java.util.Optional;

public interface IDataProcessorFactory
{
    Optional<IDataProcessor> getProcessor(ISubRecording subRecording);
}
