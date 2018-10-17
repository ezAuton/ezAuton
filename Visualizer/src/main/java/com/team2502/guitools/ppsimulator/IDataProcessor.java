package com.team2502.guitools.ppsimulator;

import javafx.animation.KeyFrame;

public interface IDataProcessor
{
//    void initData(IRecording data);

    void initEnvironment(IEnvironment environment);

    void forKeyFrame(KeyFrame keyFrame);
}