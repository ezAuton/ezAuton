package org.github.ezauton.ezauton.robot.implemented;

import org.github.ezauton.ezauton.actuators.IVelocityMotor;
import org.github.ezauton.ezauton.localization.IRotationalLocationEstimator;
import org.github.ezauton.ezauton.localization.ITranslationalLocationEstimator;
import org.github.ezauton.ezauton.robot.ITankRobotConstants;
import org.github.ezauton.ezauton.robot.subsystems.TranslationalLocationDriveable;
import org.github.ezauton.ezauton.trajectory.geometry.ImmutableVector;
import org.github.ezauton.ezauton.utils.MathUtils;

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
        ImmutableVector wheelVelocities = getWheelVelocities(speed, loc);

        double left = wheelVelocities.get(0);
        leftMotor.runVelocity(left);

        double right = wheelVelocities.get(1);
        rightMotor.runVelocity(right);

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

            if(MathUtils.Algebra.between(v_rMin, v_r, v_rMax))
            {
                score = Math.abs(v_lMax + v_r);
                bestVector = new ImmutableVector(v_lMax, v_r);
            }

            v_r = v_lMin * velLeftToRightRatio;
            if(MathUtils.Algebra.between(v_rMin, v_r, v_rMax))
            {
                double tempScore = Math.abs(v_lMin + v_r);
                if(tempScore > score)
                {
                    score = tempScore;
                    bestVector = new ImmutableVector(v_lMin, v_r);
                }
            }

            double v_l = v_rMax * velRightToLeftRatio;
            if(MathUtils.Algebra.between(v_lMin, v_l, v_lMax))
            {
                double tempScore = Math.abs(v_lMax + v_l);
                if(tempScore > score)
                {
                    score = tempScore;
                    bestVector = new ImmutableVector(v_l, v_rMax);
                }
            }

            v_l = v_rMin * velRightToLeftRatio;
            if(MathUtils.Algebra.between(v_lMin, v_l, v_lMax))
            {
                double tempScore = Math.abs(v_lMin + v_l);
                if(tempScore > score)
                {
                    bestVector = new ImmutableVector(v_l, v_rMin);
                }
            }

            if(bestVector == null)
            {
                throw new NullPointerException("bestVector is null! (wtf)"); //TODO: More informative error message
            }

            if(bestVector.get(0) < 0 && bestVector.get(1) < 0)
            {
                System.err.println("Robot is going backwards!");
            }


        }

        return bestVector;
    }
}
