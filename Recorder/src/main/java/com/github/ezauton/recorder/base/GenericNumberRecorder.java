package com.github.ezauton.recorder.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.ezauton.core.utils.Clock;
import com.github.ezauton.recorder.SequentialDataRecorder;
import com.github.ezauton.recorder.base.frame.GenericNumberFrame;
import com.github.ezauton.recorder.base.frame.PurePursuitFrame;

import javax.print.attribute.HashAttributeSet;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;

public class GenericNumberRecorder extends SequentialDataRecorder<GenericNumberFrame> {

    @JsonIgnore
    private HashMap<String, DoubleSupplier> namedNumberProducers;

    public GenericNumberRecorder(String name, Clock clock, HashMap<String, DoubleSupplier> namedNumberProducers) {
        super(name,clock);
        this.namedNumberProducers = namedNumberProducers;
    }

    public GenericNumberRecorder() {}

    @Override
    public boolean checkForNewData() {
        dataFrames.add(new GenericNumberFrame(
                stopwatch.read(TimeUnit.MILLISECONDS),
                updateNumbers()

        ));
        return true;
    }

    public HashMap<String, Double> updateNumbers() {
        HashMap<String, Double> ret = new HashMap<>();
        namedNumberProducers.forEach((key, value) -> ret.put(key, value.getAsDouble()));
        return ret;
    }
}
