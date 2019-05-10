package com.github.ezauton.core.action;

import com.github.ezauton.core.localization.RotationalLocationEstimator;
import com.github.ezauton.core.localization.TranslationalLocationEstimator;
import com.github.ezauton.core.pathplanning.ramsete.RamseteMovementStrategy;
import com.github.ezauton.core.robot.implemented.TankRobotTransLocDriveable;

import java.util.concurrent.TimeUnit;

public class RamseteAction extends PeriodicAction {

    private final RamseteMovementStrategy ramseteMovementStrategy;
    private final TranslationalLocationEstimator translationalLocationEstimator;
    private final RotationalLocationEstimator rotationalLocationEstimator;
    private final TankRobotTransLocDriveable tankRobotTransLocDriveable;
    private RamseteMovementStrategy.Pose currentPose;

    public RamseteAction(long period, TimeUnit timeUnit, RamseteMovementStrategy ramseteMovementStrategy, TranslationalLocationEstimator translationalLocationEstimator, RotationalLocationEstimator rotationalLocationEstimator, TankRobotTransLocDriveable tankRobotTransLocDriveable) {
        super(period, timeUnit);


        this.ramseteMovementStrategy = ramseteMovementStrategy;
        this.translationalLocationEstimator = translationalLocationEstimator;
        this.rotationalLocationEstimator = rotationalLocationEstimator;
        this.tankRobotTransLocDriveable = tankRobotTransLocDriveable;
    }

    @Override
    protected void execute() throws Exception {
        double x = translationalLocationEstimator.estimateLocation().get(0);
        double y = translationalLocationEstimator.estimateLocation().get(1);
        double theta = rotationalLocationEstimator.estimateHeading();
        currentPose = new RamseteMovementStrategy.Pose(x, y, theta);
        RamseteMovementStrategy.Output newOutput = ramseteMovementStrategy.recalculate(this.getStopwatch().read(TimeUnit.MILLISECONDS) / 1000D, currentPose);
        tankRobotTransLocDriveable.driveEachMotor(newOutput);
    }

    @Override
    protected boolean isFinished() throws Exception {
        return ramseteMovementStrategy.isFinished(currentPose);
//        return false;
    }
}
