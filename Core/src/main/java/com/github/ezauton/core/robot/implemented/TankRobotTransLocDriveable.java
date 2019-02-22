package com.github.ezauton.core.robot.implemented;

import com.github.ezauton.core.actuators.IVelocityMotor;
import com.github.ezauton.core.localization.IRotationalLocationEstimator;
import com.github.ezauton.core.robot.ITankRobotConstants;
import com.github.ezauton.core.robot.subsystems.TranslationalLocationDriveable;
import com.github.ezauton.core.trajectory.geometry.ImmutableVector;
import com.github.ezauton.core.utils.MathUtils;
import com.github.ezauton.core.localization.ITranslationalLocationEstimator;

/**
 * Describes the kinematics for a robot with a tank drivetrain
 */
public class TankRobotTransLocDriveable implements TranslationalLocationDriveable
{

    /**
     * The minimum curvature, below which we are driving on a straight line
     */
    private static final double THRESHOLD_CURVATURE = 0.001F;

    private final IVelocityMotor leftMotor;
    private final IVelocityMotor rightMotor;
    private final ITranslationalLocationEstimator translationalLocationEstimator;
    private final IRotationalLocationEstimator rotationalLocationEstimator;
    private final ITankRobotConstants tankRobotConstants;
    private double lastLeftTarget;
    private double lastRightTarget;

    /**
     * Describes the kinematics to drive a tank-drive robot along an arc to get to a goal point
     *
     * @param leftMotor                      The motor on the left side
     * @param rightMotor                     The motor on the right side
     * @param translationalLocationEstimator An estimator for our absolute location
     * @param rotationalLocationEstimator    An estimator for our heading
     * @param tankRobotConstants             A data class containing constants regarding the structure of the tank drive robot, such as lateral wheel distance
     */
    public TankRobotTransLocDriveable(IVelocityMotor leftMotor, IVelocityMotor rightMotor, ITranslationalLocationEstimator translationalLocationEstimator, IRotationalLocationEstimator rotationalLocationEstimator, ITankRobotConstants tankRobotConstants)
    {
        this.leftMotor = leftMotor;
        this.rightMotor = rightMotor;
        this.translationalLocationEstimator = translationalLocationEstimator;
        this.rotationalLocationEstimator = rotationalLocationEstimator;
        this.tankRobotConstants = tankRobotConstants;
    }

    /**
     * Move the robot to a target location. Ideally, this would be run continuously.
     *
     * @param speed The maximum speed of the robot
     * @param loc   The absolute coordinates of the target location
     * @return True
     */
    @Override
    public boolean driveTowardTransLoc(double speed, ImmutableVector loc)
    {
        if(loc.getDimension() != 2 ) throw new IllegalArgumentException("target location must be in R2");
        if(!Double.isFinite(loc.get(0)) || !Double.isFinite(loc.get(1))) throw new IllegalArgumentException("target location "+loc+" must contain real, finite numbers");

        ImmutableVector wheelVelocities = getWheelVelocities(speed, loc);

        lastLeftTarget = wheelVelocities.get(0);

        leftMotor.runVelocity(lastLeftTarget);

        lastRightTarget = wheelVelocities.get(1);
        rightMotor.runVelocity(lastRightTarget);

        return true; // always possible //TODO: Maybe sometimes return false?
    }

    /**
     * Drive the robot at a speed
     *
     * @param speed The target speed, where a positive value is forwards and a negative value is backwards
     * @return True, it is always possible to drive straight
     */
    @Override
    public boolean driveSpeed(double speed)
    {
        leftMotor.runVelocity(speed);
        rightMotor.runVelocity(speed);
        return true;
    }

    /**
     * Calculate how to get to a target location given a maximum speed
     *
     * @param speed The maximum speed (not velocity) the robot is allowed to go
     * @param loc   THe absolute coordinates of the goal point
     * @return The wheel speeds in a vector where the 0th element is the left wheel speed and the 1st element is the right wheel speed
     */
    private ImmutableVector getWheelVelocities(double speed, ImmutableVector loc)
    {
        ImmutableVector relativeCoord = MathUtils.LinearAlgebra.absoluteToRelativeCoord(loc, translationalLocationEstimator.estimateLocation(), rotationalLocationEstimator.estimateHeading());
        double curvature = MathUtils.calculateCurvature(relativeCoord);
        ImmutableVector bestVector = null;

        double v_lMax = speed;
        double v_rMax = speed;
        double v_lMin = -speed;
        double v_rMin = -speed;

        double lateralWheelDistance = tankRobotConstants.getLateralWheelDistance();


        if(Math.abs(curvature) < THRESHOLD_CURVATURE) // if we are a straight line ish (lines are not curvy -> low curvature)
        {
            return new ImmutableVector(v_lMax, v_rMax);
        }
        else // if we need to go in a circle, we should calculate the wheel velocities so we hit our target radius AND our target tangential speed
        {

            // Formula for differential drive radius of cricle
            // r = L/2 * (vl + vr)/(vr - vl)
            // 2(vr - vl) * r = L(vl + vr)
            // L*vl + L*vr - 2r*vr + 2r*vl = 0
            // vl(L+2r) + vr(L-2r) = 0
            // vl(L+2r) = -vr(L-2r)
            // vl/vr = -(L+2r)/(L-2r)
            double r = 1 / curvature;

            double velLeftToRightRatio = -(lateralWheelDistance + 2 * r) / (lateralWheelDistance - 2 * r);
            double velRightToLeftRatio = 1 / velLeftToRightRatio; // invert the ratio

            // This first big repetitive section is just finding the largest possible velocities while maintaining a ratio.
            double score = Double.MIN_VALUE;

            double v_r = v_lMax * velLeftToRightRatio;

            if(MathUtils.Algebra.between(v_rMin, v_r, v_rMax) || MathUtils.Algebra.between(v_rMax, v_r,v_rMin))
            {
                score = Math.abs(v_lMax + v_r);
                bestVector = new ImmutableVector(v_lMax, v_r);
            }

            v_r = v_lMin * velLeftToRightRatio;
            if(MathUtils.Algebra.between(v_rMin, v_r, v_rMax) || MathUtils.Algebra.between(v_rMax, v_r,v_rMin))
            {
                double tempScore = Math.abs(v_lMin + v_r);
                if(tempScore > score)
                {
                    score = tempScore;
                    bestVector = new ImmutableVector(v_lMin, v_r);
                }
            }

            double v_l = v_rMax * velRightToLeftRatio;
            if(MathUtils.Algebra.between(v_lMin, v_l, v_lMax) || MathUtils.Algebra.between(v_lMax, v_l, v_lMin))
            {
                double tempScore = Math.abs(v_lMax + v_l);
                if(tempScore > score)
                {
                    score = tempScore;
                    bestVector = new ImmutableVector(v_l, v_rMax);
                }
            }

            v_l = v_rMin * velRightToLeftRatio;
            if(MathUtils.Algebra.between(v_lMin, v_l, v_lMax) || MathUtils.Algebra.between(v_lMax, v_l, v_lMin))
            {
                double tempScore = Math.abs(v_lMin + v_l);
                if(tempScore > score)
                {
                    bestVector = new ImmutableVector(v_l, v_rMin);
                }
            }

            if(bestVector == null)
            {
                String s = "bestVector is null! input: {speed: " + speed + ", loc: " + loc + "}";
                throw new NullPointerException(s); //TODO: More informative error message
            }

            if(bestVector.get(0) < 0 && bestVector.get(1) < 0)
            {
                System.err.println("Robot is going backwards!");
            }


        }

        return bestVector;
    }

    public double getLastLeftTarget()
    {
        return lastLeftTarget;
    }

    public double getLastRightTarget()
    {
        return lastRightTarget;
    }
}
