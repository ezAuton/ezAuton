package com.github.ezauton.recorder.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.ezauton.core.localization.IRotationalLocationEstimator;
import com.github.ezauton.core.localization.ITranslationalLocationEstimator;
import com.github.ezauton.core.utils.IClock;
import com.github.ezauton.recorder.SequentialDataRecorder;
import com.github.ezauton.recorder.base.frame.RobotStateFrame;

import java.util.concurrent.TimeUnit;

public class RobotStateRecorder extends SequentialDataRecorder<RobotStateFrame> {

    private static int instanceCounter = 0;
    @JsonIgnore
    private ITranslationalLocationEstimator posEstimator;
    @JsonIgnore
    private IRotationalLocationEstimator rotEstimator;
    private double width;
    private double height;

    public RobotStateRecorder(String name, IClock clock, ITranslationalLocationEstimator posEstimator, IRotationalLocationEstimator rotEstimator, double width, double length) {
        super(name, clock);
        this.posEstimator = posEstimator;
        this.rotEstimator = rotEstimator;
        this.width = width;
        this.height = length;
    }

    public RobotStateRecorder(IClock clock, ITranslationalLocationEstimator posEstimator, IRotationalLocationEstimator rotEstimator, double width, double length) {
        this("RobotStateRecorder_" + instanceCounter++, clock, posEstimator, rotEstimator, width, length);
    }

    private RobotStateRecorder() {
        super();
    }

    @Override
    public boolean checkForNewData() {
        dataFrames.add(new RobotStateFrame(
                stopwatch.read(TimeUnit.MILLISECONDS),
                posEstimator.estimateLocation(),
                rotEstimator.estimateHeading(),
                width,
                height,
                posEstimator.estimateAbsoluteVelocity()
        ));
        return true;
    }

//    @Override
//    public IDataProcessor createDataProcessor()
//    {
//        return new RobotStateDataProcessor(this);
//    }
}
