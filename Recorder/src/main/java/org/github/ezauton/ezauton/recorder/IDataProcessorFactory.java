package org.github.ezauton.ezauton.recorder;

import org.github.ezauton.ezauton.visualizer.IDataProcessor;

public interface IDataProcessorFactory
{
    IDataProcessor getProcessor(ISubRecording subRecording);
}
