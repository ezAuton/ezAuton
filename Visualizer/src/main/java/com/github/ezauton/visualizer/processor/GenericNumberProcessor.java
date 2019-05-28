package com.github.ezauton.visualizer.processor;

import com.github.ezauton.recorder.base.GenericNumberRecorder;
import com.github.ezauton.recorder.base.frame.GenericNumberFrame;
import com.github.ezauton.visualizer.util.DataProcessor;
import com.github.ezauton.visualizer.util.Environment;
import javafx.animation.Interpolator;
import javafx.animation.KeyValue;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.util.*;

public class GenericNumberProcessor implements DataProcessor {

    private final HashMap<String, Label> nameLabelHashMap = new HashMap<>();
    private final HashMap<String, Label> valueLabelHashMap = new HashMap<>();
    private final GenericNumberRecorder gnRec;

    public GenericNumberProcessor(GenericNumberRecorder rec) {
        this.gnRec = rec;

        gnRec.getDataFrames().get(0).getNamedNumbers().forEach((name, num) -> {
            nameLabelHashMap.put(name, new Label(name));
            valueLabelHashMap.put(name, new Label(num.toString()));
        });
    }

    @Override
    public void initEnvironment(Environment environment) {
        GridPane dataGridPane = environment.getDataGridPane(gnRec.getName());
        ArrayList<String> sortedNames = new ArrayList<>(nameLabelHashMap.keySet());
        sortedNames.sort(String::compareToIgnoreCase);
        for (int i = 0; i < sortedNames.size(); i++) {
            String name = sortedNames.get(i);
            dataGridPane.addRow(i, nameLabelHashMap.get(name), valueLabelHashMap.get(name));
        }
    }

    @Override
    public Map<Double, List<KeyValue>> generateKeyValues(Interpolator interpolator) {
        HashMap<Double, List<KeyValue>> ret = new HashMap<>();
        for (GenericNumberFrame dataFrame : gnRec.getDataFrames()) {
            List<KeyValue> keyValues = new ArrayList<>();
            for (String name : valueLabelHashMap.keySet()) {
                keyValues.add(new KeyValue(valueLabelHashMap.get(name).textProperty(), dataFrame.getNamedNumbers().get(name).toString()));
            }
            ret.put(dataFrame.getTime(), keyValues);
        }
        return ret;
    }
}
