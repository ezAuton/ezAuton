package org.github.ezauton.ezauton.recorder.base.frame;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.ezauton.ezauton.recorder.SequentialDataFrame;
import org.github.ezauton.ezauton.recorder.base.TankDriveableRecorder;

import java.io.Serializable;

public class TankDriveableFrame extends SequentialDataFrame implements Serializable
{

    @JsonProperty
    private double attemptLeftVel;

    @JsonProperty
    private double attemptRightVel;

    public TankDriveableFrame(double time, double attemptLeftVel, double attemptRightVel)
    {
        super(time);
        this.attemptLeftVel = attemptLeftVel;
        this.attemptRightVel = attemptRightVel;
    }

    public double getAttemptLeftVel()
    {
        return attemptLeftVel;
    }

    public double getAttemptRightVel()
    {
        return attemptRightVel;
    }

    private TankDriveableFrame(){}

    @Override
    public String toString() {
        return "TankDriveableFrame{" +
                "attemptLeftVel=" + attemptLeftVel +
                ", attemptRightVel=" + attemptRightVel +
                '}';
    }
}
