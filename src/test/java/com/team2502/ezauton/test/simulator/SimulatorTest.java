package com.team2502.ezauton.test.simulator;

import com.team2502.ezauton.localization.TankRobotEncoderRotationEstimator;
import org.junit.Test;

public class SimulatorTest
{

    @Test
    public void testStraight()
    {
        SimulatedTankRobot robot = new SimulatedTankRobot(1, 0.2D, SimulatedTankRobot.NORM_DT);
        TankRobotEncoderRotationEstimator encoderRotationEstimator = new TankRobotEncoderRotationEstimator(robot.getLeft(), robot.getRight(), robot);
        encoderRotationEstimator.reset();
        for(int i = 0; i < 1000; i++)
        {
            robot.runMotorsVel(10, 10);
            encoderRotationEstimator.update();
        }
        System.out.println("encoderRotationEstimator = " + encoderRotationEstimator.estimateLocation());
    }
}
