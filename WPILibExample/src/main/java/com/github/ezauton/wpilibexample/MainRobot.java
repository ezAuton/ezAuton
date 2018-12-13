package com.github.ezauton.wpilibexample;

import com.github.ezauton.wpilibexample.actions.ActionSimpleAuton;
import edu.wpi.first.wpilibj.IterativeRobot;

public class MainRobot extends IterativeRobot
{
    private RobotData robotData;
    private DrivetrainSubsystem drivetrainSubsystem;

    @Override
    public void robotInit()
    {
        this.robotData = new RobotData(null,null,null,null,
                null,null,null,null,1.0);
        drivetrainSubsystem = new DrivetrainSubsystem(robotData);
    }

    @Override
    public void autonomousInit()
    {
        ActionSimpleAuton action = new ActionSimpleAuton(robotData, drivetrainSubsystem);

        // way 1
        action.schedule();

        // way 2
//        Scheduler.getInstance().add(new CommandCreator(action));
    }
}
