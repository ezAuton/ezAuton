package com.team2502.ezauton.robot.implemented;

import com.team2502.ezauton.actuators.IVelocityMotor;
import com.team2502.ezauton.localization.IRotationalLocationEstimator;
import com.team2502.ezauton.localization.ITranslationalLocationEstimator;
import com.team2502.ezauton.robot.ITankRobotConstants;
import com.team2502.ezauton.robot.subsystems.TranslationalLocationDriveable;
import com.team2502.ezauton.trajectory.geometry.ImmutableVector;
import com.team2502.ezauton.utils.MathUtils;

/**
 * A robot that
 */
public class TankRobotTransLocDriveable implements TranslationalLocationDriveable
{

    private static final double THRESHOLD_CURVATURE = 0.001F;
    private final IVelocityMotor leftMotor;
    private final IVelocityMotor rightMotor;
    private final ITranslationalLocationEstimator translationalLocationEstimator;
    private final IRotationalLocationEstimator rotationalLocationEstimator;
    private final ITankRobotConstants tankRobotConstants;

    public TankRobotTransLocDriveable(IVelocityMotor leftMotor, IVelocityMotor rightMotor, ITranslationalLocationEstimator translationalLocationEstimator, IRotationalLocationEstimator rotationalLocationEstimator, ITankRobotConstants tankRobotConstants)
    {
        this.leftMotor = leftMotor;
        this.rightMotor = rightMotor;
        this.translationalLocationEstimator = translationalLocationEstimator;
        this.rotationalLocationEstimator = rotationalLocationEstimator;
        this.tankRobotConstants = tankRobotConstants;
    }

    @Override
    public boolean driveTowardTransLoc(double speed, ImmutableVector loc)
    {
        ImmutableVector wheelVelocities = getWheelVelocities(speed, loc);

        double left = wheelVelocities.get(0);
        leftMotor.runVelocity(left);

        double right = wheelVelocities.get(1);
        rightMotor.runVelocity(right);

        return true; // always possible
    }

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
        else // if we need to go in a circle
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


            if(bestVector.get(0) < 0 && bestVector.get(1) < 0)
            {
                System.err.println("Robot is going backwards!");
            }

            if(bestVector == null)
            {
                throw new NullPointerException("bestVector is null! (wtf)");
            }

        }

        return bestVector;
    }
}
