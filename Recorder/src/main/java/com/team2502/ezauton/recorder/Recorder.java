package com.team2502.ezauton.recorder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Recorder implements Serializable
{
    private List<RecordFrame> recordFrames = new ArrayList<>();

    public void add(RecordFrame recordFrame)
    {
        this.recordFrames.add(recordFrame);
    }

    public List<RecordFrame> getRecordFrames()
    {
        return recordFrames;
    }
}
