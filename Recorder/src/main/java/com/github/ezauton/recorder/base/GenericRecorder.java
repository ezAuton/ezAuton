package com.github.ezauton.recorder.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.ezauton.core.utils.Clock;
import com.github.ezauton.recorder.SequentialDataRecorder;
import com.github.ezauton.recorder.base.frame.GenericFrame;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class GenericRecorder extends SequentialDataRecorder<GenericFrame> {

    @JsonIgnore
    private HashMap<String, Supplier> dataSuppliers;

    public GenericRecorder(String name, Clock clock, HashMap<String, Supplier> dataSuppliers) {
        super(name,clock);
        this.dataSuppliers = dataSuppliers;
    }

    public GenericRecorder() {}

    @Override
    public boolean checkForNewData() {
        dataFrames.add(new GenericFrame(
                stopwatch.read(TimeUnit.MILLISECONDS),
                updateData()

        ));
        return true;
    }

    public HashMap<String, Object> updateData() {
        HashMap<String, Object> ret = new HashMap<>();
        dataSuppliers.forEach((key, value) -> ret.put(key, value.get()));
        return ret;
    }
}
