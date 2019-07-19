package com.github.ezauton.recorder.base.frame;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.ezauton.conversion.ScalarVector;
import com.github.ezauton.recorder.SequentialDataFrame;

public class PurePursuitFrame extends SequentialDataFrame {
    @JsonProperty
    private double lookahead;

    @JsonProperty
    private ScalarVector closestPoint;

    @JsonProperty
    private ScalarVector goalPoint;

    @JsonProperty
    private double dCP;

    @JsonProperty
    private int currentSegmentIndex;

    public PurePursuitFrame(double time, double lookahead, ScalarVector closestPoint, ScalarVector goalPoint, double dCP, int currentSegmentIndex) {
        super(time);

        this.lookahead = lookahead;
        this.closestPoint = closestPoint;
        this.goalPoint = goalPoint;
        this.dCP = dCP;
        this.currentSegmentIndex = currentSegmentIndex;
    }

    private PurePursuitFrame() {
    }

    public double getLookahead() {
        return lookahead;
    }

    public ScalarVector getClosestPoint() {
        return closestPoint;
    }

    public ScalarVector getGoalPoint() {
        return goalPoint;
    }

    public double getdCP() {
        return dCP;
    }

    public int getCurrentSegmentIndex() {
        return currentSegmentIndex;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PurePursuitFrame{");
        sb.append("lookahead=").append(lookahead);
        sb.append(", closestPoint=").append(closestPoint);
        sb.append(", goalPoint=").append(goalPoint);
        sb.append(", dCP=").append(dCP);
        sb.append(", currentSegmentIndex=").append(currentSegmentIndex);
        sb.append('}');
        return sb.toString();
    }
}
