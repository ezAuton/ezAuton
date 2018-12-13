package com.github.ezauton.wpilibexample.actions;

import com.github.ezauton.wpilibexample.DrivetrainSubsystem;
import org.github.ezauton.ezauton.action.TimedPeriodicAction;

import java.util.concurrent.TimeUnit;

public class ActionGoStraightVoltage extends TimedPeriodicAction
{

    private final DrivetrainSubsystem subsystem;

    public ActionGoStraightVoltage(DrivetrainSubsystem subsystem)
    {
        super(3, TimeUnit.SECONDS);
        this.subsystem = subsystem;
    }

    @Override
    protected void execute() {
        subsystem.driveVolt(0.5,0.5);
    }

}
