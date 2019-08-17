package com.github.ezauton.recorder.base.frame;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.ezauton.recorder.SequentialDataFrame;

import java.util.HashMap;

public class GenericFrame extends SequentialDataFrame {

    @JsonProperty
    private HashMap<String, Object> namedData;

    public GenericFrame(double time, HashMap<String, Object> namedData) {
        super(time);
        this.namedData = namedData;
    }

    private GenericFrame() {}

    public HashMap<String, Object> getNamedData() {
        return namedData;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GenericNumberFrame{");
        sb.append("namedData=").append(namedData);
        sb.append('}');
        return sb.toString();
    }
}
