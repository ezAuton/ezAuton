package org.github.ezauton.ezauton.recorder.base;

import org.github.ezauton.ezauton.recorder.SequentialDataRecorder;
import org.github.ezauton.ezauton.recorder.base.frame.TankDriveableFrame;
import org.github.ezauton.ezauton.robot.implemented.TankRobotTransLocDriveable;
import org.github.ezauton.ezauton.visualizer.IDataProcessor;

import java.util.concurrent.TimeUnit;

public class TankDriveableRecorder extends SequentialDataRecorder<TankDriveableFrame>
{

    private final TankRobotTransLocDriveable transLocDriveable;

    public TankDriveableRecorder(TankRobotTransLocDriveable transLocDriveable)
    {
        this.transLocDriveable = transLocDriveable;
    }


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

    @Override
    public IDataProcessor createDataProcessor()
    {
        return null;
    }
}
