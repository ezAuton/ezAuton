package org.github.ezauton.ezauton.test.simulator;

import org.github.ezauton.ezauton.actuators.IVelocityMotor;
import org.github.ezauton.ezauton.actuators.implementations.BaseSimulatedMotor;
import org.github.ezauton.ezauton.actuators.implementations.BoundedVelocityProcessor;
import org.github.ezauton.ezauton.actuators.implementations.RampUpVelocityProcessor;
import org.github.ezauton.ezauton.actuators.implementations.StaticFrictionVelocityProcessor;
import org.github.ezauton.ezauton.localization.Updateable;
import org.github.ezauton.ezauton.localization.UpdateableGroup;
import org.github.ezauton.ezauton.localization.sensors.ITranslationalDistanceSensor;
import org.github.ezauton.ezauton.robot.ITankRobotConstants;
import org.github.ezauton.ezauton.utils.IClock;
import org.github.ezauton.ezauton.utils.Stopwatch;

import java.util.concurrent.TimeUnit;

public class SimulatedTankRobot implements ITankRobotConstants, Updateable
{

    public static final double NORM_DT = 0.02D;

    private final double lateralWheelDistance;

    private final IVelocityMotor leftMotor;
    private final IVelocityMotor rightMotor;

    private final BaseSimulatedMotor baseLeftSimulatedMotor;
    private final BaseSimulatedMotor baseRightSimulatedMotor;
    private final Stopwatch stopwatch;
    public StringBuilder log = new StringBuilder("t, v_l, v_r\n");
    private UpdateableGroup toUpdate = new UpdateableGroup();

    /**
     * @param lateralWheelDistance The lateral wheel distance between the wheels of the robot
     * @param clock                The clock that the simulated tank robot is using
     * @param maxAccel             The max acceleration of the motors
     * @param minVel               The minimum velocity the robot can continuously drive at (i.e. the robot cannot drive at 0.0001 ft/s)
     */
    public SimulatedTankRobot(double lateralWheelDistance, IClock clock, double maxAccel, double minVel, double maxVel)
    {
        stopwatch = new Stopwatch(clock);
        stopwatch.init();

        baseLeftSimulatedMotor = new BaseSimulatedMotor(clock);
        this.leftMotor = buildMotor(baseLeftSimulatedMotor, clock, maxAccel, minVel, maxVel);

        baseRightSimulatedMotor = new BaseSimulatedMotor(clock);
        this.rightMotor = buildMotor(baseRightSimulatedMotor, clock, maxAccel, minVel, maxVel);

        this.lateralWheelDistance = lateralWheelDistance;

    }


    public IVelocityMotor getLeftMotor()
    {
        return leftMotor;
    }

    public IVelocityMotor getRightMotor()
    {
        return rightMotor;
    }

    public void run(double left, double right)
    {
        leftMotor.runVelocity(left);
        rightMotor.runVelocity(right);
    }

    private BoundedVelocityProcessor buildMotor(BaseSimulatedMotor baseSimulatedMotor, IClock clock, double maxAccel, double minVel, double maxVel)
    {
        RampUpVelocityProcessor leftRampUpMotor = new RampUpVelocityProcessor(baseSimulatedMotor, clock, maxAccel);
        toUpdate.add(leftRampUpMotor);

        StaticFrictionVelocityProcessor leftSF = new StaticFrictionVelocityProcessor(baseSimulatedMotor, leftRampUpMotor, minVel);
        return new BoundedVelocityProcessor(leftSF, maxVel);
    }

    public ITranslationalDistanceSensor getLeftDistanceSensor()
    {
        return baseLeftSimulatedMotor;
    }

    public ITranslationalDistanceSensor getRightDistanceSensor()
    {
        return baseRightSimulatedMotor;
    }

    public double getLateralWheelDistance()
    {
        return lateralWheelDistance;
    }

    @Override
    public boolean update()
    {
        long read = stopwatch.read(TimeUnit.SECONDS);
        log.append(read).append(", ").append(baseLeftSimulatedMotor.getVelocity()).append(", ").append(baseRightSimulatedMotor.getVelocity()).append("\n");
        toUpdate.update();
        return true;
    }
}
