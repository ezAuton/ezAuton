package com.team2502.ezauton.pathplanning;

import com.team2502.ezauton.utils.BinarySearch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MotionProfile
{
    private final List<MotionSegment> motionSegments;

    public MotionProfile(List<MotionSegment> motionSegments)
    {
        this.motionSegments = new ArrayList<>(motionSegments);
    }

    public MotionProfile(MotionSegment... motionSegments)
    {
        this.motionSegments = new ArrayList<>(Arrays.asList(motionSegments));
    }

    public double getSpeed(double position)
    {
        MotionSegment motionSegment = getMotionSegment(position);
        double time = motionSegment.getFrom().timeByPos(position);
        MotionState extrapolated = motionSegment.getFrom().extrapolateTime(time);
        return extrapolated.getSpeed();
    }

    public MotionSegment getMotionSegment(double position)
    {
        BinarySearch<MotionSegment> binarySearch = new BinarySearch<>(motionSegments);

        return binarySearch.search(motionSegment -> {
            if(position < motionSegment.getFrom().getPosition())
            {
                return BinarySearch.SearchEntryResult.LOW;
            }
            if(position > motionSegment.getTo().getPosition())
            {
                return BinarySearch.SearchEntryResult.HIGH;
            }
            return BinarySearch.SearchEntryResult.CORRECT;

        });
    }
}