package com.github.ezauton.recorder.base;

import com.github.ezauton.core.utils.IClock;
import com.github.ezauton.recorder.SequentialDataRecorder;
import com.github.ezauton.recorder.base.frame.TankDriveableFrame;
import com.github.ezauton.core.robot.implemented.TankRobotTransLocDriveable;

import java.util.concurrent.TimeUnit;

public class TankDriveableRecorder extends SequentialDataRecorder<TankDriveableFrame>
{

    private TankRobotTransLocDriveable transLocDriveable;

    public TankDriveableRecorder(String name, IClock clock, TankRobotTransLocDriveable transLocDriveable)
    {
        super(name, clock);
        this.transLocDriveable = transLocDriveable;
    }

    private TankDriveableRecorder(){}


    @Override
    public boolean checkForNewData()
    {
        dataFrames.add(new TankDriveableFrame(
                stopwatch.read(TimeUnit.MILLISECONDS),
                transLocDriveable.getLastLeftTarget(),
                transLocDriveable.getLastRightTarget()
        ));
        return true;
    }

//    @Override
//    public IDataProcessor createDataProcessor()
//    {
//        return null;
//    }
}
