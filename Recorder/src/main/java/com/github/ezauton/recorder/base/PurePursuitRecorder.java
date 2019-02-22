package com.github.ezauton.recorder.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.ezauton.core.pathplanning.Path;
import com.github.ezauton.core.pathplanning.purepursuit.PurePursuitMovementStrategy;
import com.github.ezauton.core.utils.IClock;
import com.github.ezauton.recorder.SequentialDataRecorder;
import com.github.ezauton.recorder.base.frame.PurePursuitFrame;

import java.util.concurrent.TimeUnit;

public class PurePursuitRecorder extends SequentialDataRecorder<PurePursuitFrame> {

    private static int instanceCounter = 0;
    // contains path data, lookahead + segment data
    @JsonProperty
    private Path path;
    @JsonIgnore
    private PurePursuitMovementStrategy strat;
    private int i = 0;

    public PurePursuitRecorder(String name, IClock clock, Path path, PurePursuitMovementStrategy strat) {
        super(name, clock);
        this.path = path;
        this.strat = strat;
    }

    public PurePursuitRecorder(IClock clock, Path path, PurePursuitMovementStrategy strat) {
        this("PurePursuitRecorder_" + instanceCounter++, clock, path, strat);
    }

    public PurePursuitRecorder() {
        super("PurePursuitRecorder_" + instanceCounter++);
    }

    public Path getPath() {
        return path;
    }


    @Override
    public boolean checkForNewData() {
        if (i++ == 0) {
            stopwatch.init();
        }
        dataFrames.add(new PurePursuitFrame(stopwatch.read(TimeUnit.MILLISECONDS),
                strat.getLatestLookahead(),
                strat.getClosestPoint(),
                strat.getGoalPoint(),
                strat.getDCP(),
                strat.getPath().getSegmentOnI()));
        return true;
    }
}
