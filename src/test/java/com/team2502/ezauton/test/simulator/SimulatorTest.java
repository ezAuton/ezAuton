package com.team2502.ezauton.test.simulator;

import com.team2502.ezauton.localization.TankRobotEncoderRotationEstimator;
import com.team2502.ezauton.utils.SimulatedStopwatch;
import org.junit.Test;

public class SimulatorTest
{

    @Test
    public void testStraight()
    {
        SimulatedStopwatch stopwatch = new SimulatedStopwatch(SimulatedTankRobot.NORM_DT);
        SimulatedTankRobot robot = new SimulatedTankRobot(1, 0.2D, stopwatch);
        TankRobotEncoderRotationEstimator encoderRotationEstimator = new TankRobotEncoderRotationEstimator(robot.getLeftWheel(), robot.getRightWheel(), robot);
        encoderRotationEstimator.reset();
        for(int i = 0; i < 1000; i++)
        {
            robot.run(1,1);
            encoderRotationEstimator.update();
            stopwatch.progress();
        }
        System.out.println("encoderRotationEstimator = " + encoderRotationEstimator.estimateLocation());
    }
}
