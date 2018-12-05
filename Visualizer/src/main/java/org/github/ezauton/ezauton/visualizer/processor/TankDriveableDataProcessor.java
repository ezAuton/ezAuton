package org.github.ezauton.ezauton.visualizer.processor;

import javafx.animation.Interpolator;
import javafx.animation.KeyValue;
import javafx.scene.control.Label;
import org.github.ezauton.ezauton.recorder.base.TankDriveableRecorder;
import org.github.ezauton.ezauton.visualizer.util.IDataProcessor;
import org.github.ezauton.ezauton.visualizer.util.IEnvironment;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TankDriveableDataProcessor implements IDataProcessor
{

    private final TankDriveableRecorder recorder;
    private Label leftVel;
    private Label rightVel;

    public TankDriveableDataProcessor(TankDriveableRecorder recorder)
    {
        this.recorder = recorder;
    }

    @Override
    public void initEnvironment(IEnvironment environment)
    {
        // heading info
        leftVel = new Label("0");

        // x y loc
        rightVel = new Label("0");

        environment.getDataGridPane(recorder.getName()).addRow(0, new Label("Left vel: "), leftVel);
        environment.getDataGridPane(recorder.getName()).addRow(1, new Label("Right vel: "), rightVel);;
    }

    @Override
    public Map<Double, List<KeyValue>> generateKeyValues(Interpolator interpolator)
    {
        Map<Double, List<KeyValue>> map = new HashMap<>();
        recorder.getDataFrames()
                .forEach(frame -> {
                    KeyValue kv1 = new KeyValue(leftVel.textProperty(), String.format("%.02f", frame.getAttemptLeftVel()));
                    KeyValue kv2 = new KeyValue(rightVel.textProperty(), String.format("%.02f", frame.getAttemptRightVel()));
                    map.put(frame.getTime(), Arrays.asList(kv1, kv2));
                });
        return map;
    }
}
