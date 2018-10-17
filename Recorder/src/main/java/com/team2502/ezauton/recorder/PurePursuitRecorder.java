package com.team2502.ezauton.recorder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.team2502.ezauton.localization.Updateable;
import com.team2502.ezauton.pathplanning.Path;
import com.team2502.ezauton.pathplanning.purepursuit.PurePursuitMovementStrategy;
import com.team2502.ezauton.robot.subsystems.TranslationalLocationDriveable;
import com.team2502.ezauton.utils.IClock;
import com.team2502.ezauton.utils.Stopwatch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PurePursuitRecorder<T extends TranslationalLocationDriveable> implements ISubRecording, Updateable
{

    // contains path data, lookahead + segment data
    @JsonProperty
    private final Path path;

    @JsonIgnore
    private final PurePursuitMovementStrategy strat;

    @JsonProperty
    private final List<PurePursuitFrame> frames = new ArrayList<>();

    @JsonIgnore
    private final Stopwatch stopwatch;

    private static int instanceCounter = 0;
    private final String name;
    private int i = 0;

    public PurePursuitRecorder(Path path, PurePursuitMovementStrategy strat, IClock clock, String name)
    {
        this.path = path;
        this.strat = strat;
        this.stopwatch = new Stopwatch(clock);
        this.name = name;
    }

    public PurePursuitRecorder(Path path, PurePursuitMovementStrategy strat, IClock clock)
    {
        this(path, strat, clock, "PurePursuitRecorder_" + instanceCounter++);
    }

    public Path getPath()
    {
        return path;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getJSON()
    {
        return JsonUtils.toStringUnchecked(this);
    }

    @Override
    public boolean update()
    {
        if (i++ == 0)
        {
            stopwatch.init();
        }
        frames.add(new PurePursuitFrame(stopwatch.read(TimeUnit.SECONDS),
                                        strat.getLatestLookahead(),
                                        strat.getClosestPoint(),
                                        strat.getGoalPoint(),
                                        strat.getDCP(),
                                        0));
        return true;
    }

    public List<PurePursuitFrame> getFrames()
    {
        return frames;
    }
}
