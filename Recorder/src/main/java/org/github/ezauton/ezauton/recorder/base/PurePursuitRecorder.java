package org.github.ezauton.ezauton.recorder.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.ezauton.ezauton.pathplanning.Path;
import org.github.ezauton.ezauton.pathplanning.purepursuit.PurePursuitMovementStrategy;
import org.github.ezauton.ezauton.recorder.SequentialDataRecorder;
import org.github.ezauton.ezauton.recorder.base.frame.PurePursuitFrame;
//import org.github.ezauton.ezauton.visualizer.processor.PurePursuitDataProcessor;
import org.github.ezauton.ezauton.utils.IClock;
import org.github.ezauton.ezauton.visualizer.IDataProcessor;

import java.util.concurrent.TimeUnit;

public class PurePursuitRecorder extends SequentialDataRecorder<PurePursuitFrame>
{

    private static int instanceCounter = 0;
    // contains path data, lookahead + segment data
    @JsonProperty
    private Path path;
    @JsonIgnore
    private PurePursuitMovementStrategy strat;
    private int i = 0;

    public PurePursuitRecorder(String name, IClock clock, Path path, PurePursuitMovementStrategy strat)
    {
        super(name, clock);
        this.path = path;
        this.strat = strat;
    }

    public PurePursuitRecorder(IClock clock, Path path, PurePursuitMovementStrategy strat)
    {
        this("PurePursuitRecorder_" + instanceCounter++, clock, path, strat);
    }

    public PurePursuitRecorder()
    {
        this.name = "PurePursuitRecorder_" + instanceCounter++;
    }

    public Path getPath()
    {
        return path;
    }


    @Override
    public boolean checkForNewData()
    {
        if(i++ == 0)
        {
            stopwatch.init();
        }
        dataFrames.add(new PurePursuitFrame(stopwatch.read(TimeUnit.MILLISECONDS),
                                            strat.getLatestLookahead(),
                                            strat.getClosestPoint(),
                                            strat.getGoalPoint(),
                                            strat.getDCP(),
                                            0));
        return true;
    }

//    @Override
//    public IDataProcessor createDataProcessor()
//    {
//        return new PurePursuitDataProcessor(this);
//    }
}
