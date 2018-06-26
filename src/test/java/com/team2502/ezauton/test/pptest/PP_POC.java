package com.team2502.ezauton.test.pptest;

import com.team2502.ezauton.command.PPCommand;
import com.team2502.ezauton.localization.ITranslationalLocationEstimator;
import com.team2502.ezauton.pathplanning.PP_PathGenerator;
import com.team2502.ezauton.pathplanning.Path;
import com.team2502.ezauton.pathplanning.purepursuit.PPWaypoint;
import com.team2502.ezauton.pathplanning.purepursuit.PurePursuitMovementStrategy;
import com.team2502.ezauton.robot.implemented.TankRobotTransLocDriveable;

public class PP_POC
{

    public void exampleCommand()
    {
        PPWaypoint waypoint1 = PPWaypoint.simple2D(0, 0, 0, 3, -3);
        PPWaypoint waypoint2 = PPWaypoint.simple2D(0, 6, 5, 3, -3);
        PPWaypoint waypoint3 = PPWaypoint.simple2D(0, 12, 0, 3, -3);

        PP_PathGenerator pathGenerator = new PP_PathGenerator(waypoint1,waypoint2,waypoint3);
        Path path = pathGenerator.generate(0.05);

        PurePursuitMovementStrategy ppMoveStrat = new PurePursuitMovementStrategy(path,0.1);

        TankRobotTransLocDriveable tankRobotTransLocDriveable = new TankRobotTransLocDriveable(velocity -> {}, velocity -> {}, () -> null, () -> 0, () -> 5);
        PPCommand ppCommand = new PPCommand(ppMoveStrat, (ITranslationalLocationEstimator) () -> null, () -> 0, tankRobotTransLocDriveable);
    }
}
