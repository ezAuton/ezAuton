package com.github.ezauton.core.simulation;

import com.github.ezauton.core.actuators.VelocityMotor;
import com.github.ezauton.core.actuators.implementations.SimulatedMotor;
import com.github.ezauton.core.localization.Updateable;
import com.github.ezauton.core.localization.UpdateableGroup;
import com.github.ezauton.core.localization.estimators.TankRobotEncoderEncoderEstimator;
import com.github.ezauton.core.localization.sensors.Encoders;
import com.github.ezauton.core.localization.sensors.TranslationalDistanceSensor;
import com.github.ezauton.core.robot.TankRobotConstants;
import com.github.ezauton.core.robot.implemented.TankRobotTransLocDriveable;
import com.github.ezauton.core.utils.Clock;
import com.github.ezauton.core.utils.Stopwatch;

public class SimulatedTankRobot implements TankRobotConstants, Updateable {

    public static final double NORM_DT = 0.02D;

    private final double lateralWheelDistance;

    private final SimulatedMotor left;
    private final SimulatedMotor right;

    private final Stopwatch stopwatch;
    private final TranslationalDistanceSensor leftTDS;
    private final TranslationalDistanceSensor rightTDS;
    private final TankRobotEncoderEncoderEstimator defaultLocationEstimator;
    private final TankRobotTransLocDriveable defaultTranslationalLocationDriveable;
    //    public StringBuilder log = new StringBuilder("t, v_l, v_r\n");
    private UpdateableGroup toUpdate = new UpdateableGroup();

    /**
     * @param lateralWheelDistance The lateral wheel distance between the wheels of the robot
     * @param clock                The clock that the simulated tank robot is using
     * @param maxAccel             The max acceleration of the motors
     * @param minVel               The minimum velocity the robot can continuously drive at (i.e. the robot cannot drive at 0.0001 ft/s)
     */
    public SimulatedTankRobot(double lateralWheelDistance, Clock clock, double maxAccel, double minVel, double maxVel) {
        stopwatch = new Stopwatch(clock);
        stopwatch.init();

        left = new SimulatedMotor(clock, maxAccel, minVel, maxVel, 1);
        leftTDS = Encoders.toTranslationalDistanceSensor(1, 1, left);

        right = new SimulatedMotor(clock, maxAccel, minVel, maxVel, 1);
        rightTDS = Encoders.toTranslationalDistanceSensor(1, 1, right);

        toUpdate.add(left);
        toUpdate.add(right);
        this.lateralWheelDistance = lateralWheelDistance;

        this.defaultLocationEstimator = new TankRobotEncoderEncoderEstimator(getLeftDistanceSensor(), getRightDistanceSensor(), this);
        this.defaultTranslationalLocationDriveable = new TankRobotTransLocDriveable(left, right, defaultLocationEstimator, defaultLocationEstimator, this);

    }

    /**
     * @return A location estimator which automatically updates
     */
    public TankRobotEncoderEncoderEstimator getDefaultLocEstimator() {
        return defaultLocationEstimator;
    }

    public TankRobotTransLocDriveable getDefaultTransLocDriveable() {
        return defaultTranslationalLocationDriveable;
    }


    public VelocityMotor getLeftMotor() {
        return left;
    }

    public VelocityMotor getRightMotor() {
        return right;
    }

    public void run(double leftV, double rightV) {
        left.runVelocity(leftV);
        right.runVelocity(rightV);
    }

    public TranslationalDistanceSensor getLeftDistanceSensor() {
        return leftTDS;
    }

    public TranslationalDistanceSensor getRightDistanceSensor() {
        return rightTDS;
    }

    public double getLateralWheelDistance() {
        return lateralWheelDistance;
    }

    @Override
    public boolean update() {
//        long read = stopwatch.read(TimeUnit.SECONDS);
//        log.append(read).append(", ").append(leftTDS.getVelocity()).append(", ").append(rightTDS.getVelocity()).append("\n");
        toUpdate.update();
        defaultLocationEstimator.update();
        return true;
    }
}
