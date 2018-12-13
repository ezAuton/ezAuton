package com.github.ezauton.wpilibexample;

import com.github.ezauton.wpilibexample.actions.ActionEverythingM8;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import org.github.ezauton.ezauton.wpilib.command.CommandCreator;

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
        ActionEverythingM8 action = new ActionEverythingM8(robotData, drivetrainSubsystem);

        // way 1
        action.schedule();

        // way 2
        Scheduler.getInstance().add(new CommandCreator(action));
    }
}
