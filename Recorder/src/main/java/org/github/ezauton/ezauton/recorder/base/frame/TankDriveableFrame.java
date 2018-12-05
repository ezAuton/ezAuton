package org.github.ezauton.ezauton.recorder.base.frame;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.ezauton.ezauton.recorder.SequentialDataFrame;

import java.io.Serializable;

public class TankDriveableFrame extends SequentialDataFrame implements Serializable
{

    @JsonProperty
    private final double attemptLeftVel;

    @JsonProperty
    private final double attemptRightVel;

    public TankDriveableFrame(double time, double attemptLeftVel, double attemptRightVel)
    {
        super(time);
        this.attemptLeftVel = attemptLeftVel;
        this.attemptRightVel = attemptRightVel;
    }

    @Override
    public String toString() {
        return "TankDriveableFrame{" +
                "attemptLeftVel=" + attemptLeftVel +
                ", attemptRightVel=" + attemptRightVel +
                '}';
    }
}
