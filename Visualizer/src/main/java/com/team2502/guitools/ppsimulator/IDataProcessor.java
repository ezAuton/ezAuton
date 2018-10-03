package com.team2502.guitools.ppsimulator;

import javafx.animation.KeyFrame;

public interface IDataProcessor
{
    void initData(String json);

    void initEnvironment(IEnvironment environment);

    void forKeyFrame(KeyFrame keyFrame);
}
