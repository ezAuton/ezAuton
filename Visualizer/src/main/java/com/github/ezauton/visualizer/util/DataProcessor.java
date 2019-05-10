package com.github.ezauton.visualizer.util;

import javafx.animation.Interpolator;
import javafx.animation.KeyValue;

import java.util.List;
import java.util.Map;

public interface DataProcessor {
    void initEnvironment(Environment environment);

    Map<Double, List<KeyValue>> generateKeyValues(Interpolator interpolator);
}
