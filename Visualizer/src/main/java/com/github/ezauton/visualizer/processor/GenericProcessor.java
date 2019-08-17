package com.github.ezauton.visualizer.processor;

import com.github.ezauton.recorder.base.GenericRecorder;
import com.github.ezauton.recorder.base.frame.GenericFrame;
import com.github.ezauton.visualizer.util.DataProcessor;
import com.github.ezauton.visualizer.util.Environment;
import javafx.animation.Interpolator;
import javafx.animation.KeyValue;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenericProcessor implements DataProcessor {

    private final HashMap<String, Label> nameLabelHashMap = new HashMap<>();
    private final HashMap<String, Label> valueLabelHashMap = new HashMap<>();
    private final GenericRecorder gnRec;

    public GenericProcessor(GenericRecorder rec) {
        this.gnRec = rec;

        gnRec.getDataFrames().get(0).getNamedData().forEach((name, num) -> {
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
        for (GenericFrame dataFrame : gnRec.getDataFrames()) {
            List<KeyValue> keyValues = new ArrayList<>();
            for (String name : valueLabelHashMap.keySet()) {
                try {
                    double doubleVal = Double.parseDouble(dataFrame.getNamedData().get(name).toString());

                    // else
                    keyValues.add(new KeyValue(valueLabelHashMap.get(name).textProperty(), String.format("%.04f", doubleVal)));
                } catch (NumberFormatException e) {

                    keyValues.add(new KeyValue(valueLabelHashMap.get(name).textProperty(), dataFrame.getNamedData().get(name).toString()));
                }
            }
            ret.put(dataFrame.getTime(), keyValues);
        }
        return ret;
    }
}
