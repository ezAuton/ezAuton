package com.github.ezauton.wpilibexample;

import org.github.ezauton.ezauton.localization.Updateable;
import org.github.ezauton.ezauton.localization.estimators.TankRobotEncoderEncoderEstimator;
import org.github.ezauton.ezauton.recorder.base.RobotStateRecorder;
import org.github.ezauton.ezauton.robot.implemented.TankRobotTransLocDriveable;

public class DrivetrainSubsystem implements Updateable {

    private final TankRobotEncoderEncoderEstimator locEstimator;
    private final RobotStateRecorder robotStateRecorder;
    private final RobotData robotData;
    private final TankRobotTransLocDriveable transLocDriveable;

    public DrivetrainSubsystem(RobotData robotData) {
        this.robotData = robotData;
        this.locEstimator = new TankRobotEncoderEncoderEstimator(robotData.getDistanceSensorLeft(), robotData.getDistanceSensorRight(), robotData.getRobotConstants());
        transLocDriveable = new TankRobotTransLocDriveable(robotData.getLeftMotorVel(), robotData.getRightMotorVel(), locEstimator, locEstimator, robotData.getRobotConstants());
        this.robotStateRecorder = new RobotStateRecorder("robotstate", robotData.getClock(), locEstimator, locEstimator, robotData.getRobotConstants().getLateralWheelDistance(), robotData.getLength());
    }

    public TankRobotTransLocDriveable getTransLocDriveable()
    {
        return transLocDriveable;
    }

    public TankRobotEncoderEncoderEstimator getLocEstimator() {
        return locEstimator;
    }

    public void driveVolt(double left, double right) {
        robotData.getLeftMotorVolt().runVoltage(left);
        robotData.getRightMotorVolt().runVoltage(right);
    }

    public void driveVel(double left, double right) {
        robotData.getLeftMotorVel().runVelocity(left);
        robotData.getRightMotorVel().runVelocity(right);
    }

    @Override
    public boolean update() {
        locEstimator.update();
        robotStateRecorder.update();
        return true;
    }
}
