package org.github.ezauton.ezauton.visualizer;

import javafx.animation.KeyFrame;

public interface IDataProcessor
{
//    void initData(IRecording data);

    void initEnvironment(IEnvironment environment);

    void forKeyFrame(KeyFrame keyFrame);
}
