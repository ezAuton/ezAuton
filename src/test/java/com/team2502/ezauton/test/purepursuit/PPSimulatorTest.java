package com.team2502.ezauton.test.purepursuit;

import com.team2502.ezauton.actuators.IVelocityMotor;
import com.team2502.ezauton.command.ICommand;
import com.team2502.ezauton.command.PPCommand;
import com.team2502.ezauton.localization.TankRobotEncoderRotationEstimator;
import com.team2502.ezauton.pathplanning.IPathSegment;
import com.team2502.ezauton.pathplanning.PP_PathGenerator;
import com.team2502.ezauton.pathplanning.Path;
import com.team2502.ezauton.pathplanning.PathSegmentExtrapolated;
import com.team2502.ezauton.pathplanning.purepursuit.ILookahead;
import com.team2502.ezauton.pathplanning.purepursuit.LookaheadBounds;
import com.team2502.ezauton.pathplanning.purepursuit.PPWaypoint;
import com.team2502.ezauton.pathplanning.purepursuit.PurePursuitMovementStrategy;
import com.team2502.ezauton.robot.implemented.TankRobotTransLocDriveable;
import com.team2502.ezauton.test.simulator.SimulatedTankRobot;
import com.team2502.ezauton.trajectory.geometry.ImmutableVector;
import com.team2502.ezauton.utils.InterpolationMap;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class PPSimulatorTest
{

    private static final double LATERAL_WHEEL_DIST = 4;
    private static final double WHEEL_SIZE = 0.5;

    @Test
    public void testStraight()
    {

        PPWaypoint waypoint1 = PPWaypoint.simple2D(0, 0, 0, 3, -3);
        PPWaypoint waypoint2 = PPWaypoint.simple2D(0, 6, 5, 3, -3);
        PPWaypoint waypoint3 = PPWaypoint.simple2D(0, 20, 0, 3, -2);

        test(waypoint1, waypoint2, waypoint3);
    }

    public void testRight()
    {
        PPWaypoint waypoint1 = PPWaypoint.simple2D(0, 0, 0, 3, -3);
        PPWaypoint waypoint2 = PPWaypoint.simple2D(6, 6, 5, 3, -3);
        PPWaypoint waypoint3 = PPWaypoint.simple2D(12, 0, 0, 3, -3);

        test(waypoint1, waypoint2, waypoint3);
    }

    private void test(PPWaypoint... waypoints)
    {
        PP_PathGenerator pathGenerator = new PP_PathGenerator(waypoints);
        Path path = pathGenerator.generate(0.05);

        PurePursuitMovementStrategy ppMoveStrat = new PurePursuitMovementStrategy(path, 0.1);

        SimulatedTankRobot robot = new SimulatedTankRobot(LATERAL_WHEEL_DIST, WHEEL_SIZE, 0.05);

        IVelocityMotor leftMotor = robot.getLeftMotor();
        IVelocityMotor rightMotor = robot.getRightMotor();

        TankRobotEncoderRotationEstimator locEstimator = new TankRobotEncoderRotationEstimator(robot.getLeftWheel(), robot.getRightWheel(), robot);
        locEstimator.reset();

        ILookahead lookahead = new LookaheadBounds(1, 5, 2, 10, locEstimator);

        TankRobotTransLocDriveable tankRobotTransLocDriveable = new TankRobotTransLocDriveable(leftMotor, rightMotor, locEstimator, locEstimator, robot);

        PPCommand ppCommand = new PPCommand(ppMoveStrat, locEstimator, lookahead, tankRobotTransLocDriveable);

        List<IPathSegment> pathSegments = path.getPathSegments();
        IPathSegment pathSegment = pathSegments.get(pathSegments.size() - 1);
        PathSegmentExtrapolated extrapolated = (PathSegmentExtrapolated) pathSegment;
        double speed = extrapolated.getSpeed(19.95D);
        InterpolationMap speedInterpolator = extrapolated.getSpeedInterpolator();
        System.out.println("speedInterpolator: " + speedInterpolator.toString());

        ICommand locUpdator = new ICommand()
        {
            @Override
            public void execute()
            {
                locEstimator.update();
            }

            @Override
            public boolean isFinished()
            {
                return ppCommand.isFinished();
            }
        };

        ppCommand.testWith(locUpdator);

        double leftWheelVelocity = locEstimator.getLeftTranslationalWheelVelocity();
        Assert.assertEquals(0, leftWheelVelocity, 0.2D);

        ImmutableVector finalLoc = locEstimator.estimateLocation();
        approxEqual(waypoints[waypoints.length - 1].getLocation(), finalLoc, 0.2);
    }

    private void approxEqual(ImmutableVector a, ImmutableVector b, double epsilon)
    {
        double[] bElements = b.getElements();
        double[] aElements = a.getElements();
        for(int i = 0; i < aElements.length; i++)
        {
            Assert.assertEquals(aElements[i], bElements[i], epsilon);
        }
    }
}
