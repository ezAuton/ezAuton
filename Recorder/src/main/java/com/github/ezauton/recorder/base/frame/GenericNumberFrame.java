package com.github.ezauton.recorder.base.frame;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.ezauton.recorder.SequentialDataFrame;

import java.util.HashMap;

public class GenericNumberFrame extends SequentialDataFrame {

    @JsonProperty
    private HashMap<String, Double> namedNumbers;

    public GenericNumberFrame(double time, HashMap<String, Double> namedNumbers) {
        super(time);
        this.namedNumbers = namedNumbers;
    }

    private GenericNumberFrame() {}

    public HashMap<String, Double> getNamedNumbers() {
        return namedNumbers;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GenericNumberFrame{");
        sb.append("namedNumbers=").append(namedNumbers);
        sb.append('}');
        return sb.toString();
    }
}
