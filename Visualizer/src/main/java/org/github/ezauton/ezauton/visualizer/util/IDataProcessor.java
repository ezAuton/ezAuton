package org.github.ezauton.ezauton.visualizer.util;

import javafx.animation.Interpolator;
import javafx.animation.KeyValue;

import java.util.List;
import java.util.Map;

public interface IDataProcessor
{
    void initEnvironment(IEnvironment environment);

    Map<Double, List<KeyValue>> generateKeyValues(Interpolator interpolator);
}
