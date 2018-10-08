package com.team2502.ezauton.recorder;

import com.team2502.ezauton.localization.Updateable;
import com.team2502.ezauton.pathplanning.IPathSegment;
import com.team2502.ezauton.pathplanning.Path;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class PurePursuitRecorder implements ISubRecording, Updateable
{

    private final Path path;

    public PurePursuitRecorder(Path path)
    {
        this.path = path;
        Arrays.asList(path,)
    }

    public Path getPath()
    {
        return path;
    }

    @Override
    public String getName()
    {
        return null;
    }

    @Override
    public String getJSON()
    {
        return null;
    }

    @Override
    public boolean update()
    {
        return true;
    }
}
