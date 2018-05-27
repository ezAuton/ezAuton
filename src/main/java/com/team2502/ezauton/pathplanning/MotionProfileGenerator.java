package com.team2502.ezauton.pathplanning;

import com.team2502.ezauton.utils.MathUtils;

public class MotionProfileGenerator
{

    public static MotionProfile generate(MotionState start, MotionProfilingConstraints constraints, MotionGoalState goalState)
    {
        double startVelocity = start.getVelocity();
        double endVelocity = goalState.getEndVelocity();

        double maxVelocity = constraints.getMaxVelocity();
        double minVelocity = constraints.getMinVelocity();

        double dPos = goalState.getEndPosition() - start.getPosition();

        if(dPos < 0)
        {
            throw new IllegalArgumentException("goalState pos must be greater than the start pos");
        }

        if(!MathUtils.Algebra.bounded(minVelocity, startVelocity, maxVelocity) ||
           !MathUtils.Algebra.bounded(minVelocity, endVelocity, maxVelocity))
        {
            throw new IllegalArgumentException("end points must be within constraints!");
        }

        (startVelocity*startVelocity - endVelocity*endVelocity -2*)
        double dVelocity = endVelocity - startVelocity;

        if(dVelocity < 0)
        {
            if(endVelocity > 0)
            {

            }
            else
            {

            }
        }
        else
        {
            if(endVelocity > 0)
            {

            }
            else
            {

            }
        }
    }

    /**
     *
     * @param vi
     * @param vf
     * @param a1
     * @param a2
     * @return
     */
    private double distanceMeet(double vi, double vf, double a1, double a2, double pathLength)
    {
        if(vi)
        return (vf*vf - vi*vi - 2*a2 * pathLength)/(2*a1 -2*a2);
    }

    /**
     * Whether velocity should be changed first or last
     * @param initVel
     * @param finalVel
     * @return
     */
    private static boolean changeVelFirst(double initVel, double finalVel)
    {
        if(initVel < 0)
        {
            if(finalVel < initVel)
            {
                return true;
            }
            else if(finalVel > 0)
            {
                return true;
            }
        }
        else if(initVel > 0)
        {
            if(finalVel > initVel)
            {
                return true;
            }
            else if(finalVel < 0)
            {
                return true;
            }
        }
        return false;
    }
}
