package com.team2502.ezauton.pathplanning;

public class MotionProfileGenerator
{

    public static MotionProfile generate(MotionState start, MotionProfilingConstraints constraints, MotionGoalState goalState)
    {

        MotionState from = new MotionState(start.getPosition(), start.getSpeed(), constraints.getMaxAcceleration(), start.getTime());

        if(constraints.getMaxSpeed() <= 0)
        {
            throw new IllegalArgumentException("max speed must be greater than 0");
        }

        if(goalState.getEndSpeed() < 0)
        {
            throw new IllegalArgumentException("end speed must be positive");
        }

        double maxAcceleration = constraints.getMaxAcceleration();
        if(maxAcceleration <= 0)
        {
            throw new IllegalArgumentException("acceleration must be positive");
        }

        double maxDeceleration = constraints.getMaxDeceleration();
        if(constraints.getMaxDeceleration() >= 0)
        {
            throw new IllegalArgumentException("deceleration must be negative");
        }

        double startPosition = start.getPosition();

        double endPosition = goalState.getEndPosition();

        double dPos = endPosition - startPosition;

        if(dPos < 0)
        {
            throw new IllegalArgumentException("goalState pos must be greater than the start pos");
        }

        double maxSpeed = constraints.getMaxSpeed();

        double startSpeed = Math.max(start.getSpeed(), maxSpeed);

        double endSpeed = goalState.getEndSpeed();

        double cruiseSpeed = constraints.getMaxSpeed();

        double dAccelCruise = (cruiseSpeed * cruiseSpeed - startSpeed * startSpeed) / (2 * maxAcceleration);
        double dCruiseDecel = (endSpeed * endSpeed - cruiseSpeed * cruiseSpeed) / (2 * maxDeceleration);

        if(dAccelCruise + dCruiseDecel > dPos) // Triangular Motion Profiling
        {
            double u = endSpeed * endSpeed - startSpeed * startSpeed;
            double dDecel = (u - maxAcceleration * dPos) / (maxDeceleration - maxAcceleration);
            double dAccel = dPos - dDecel;

            MotionState to = from.extrapolatePos(from.getPosition() + dAccel);
            MotionSegment accel = new MotionSegment(from, to);

            MotionState decelFirst = new MotionState(to.getPosition(), to.getSpeed(), constraints.getMaxDeceleration(), to.getTime());
            MotionSegment decel = new MotionSegment(decelFirst, decelFirst.extrapolatePos(to.getPosition() + dDecel));
            return new MotionProfile(accel, decel);
        }
        else // Trapezoidal Motion Profiling
        {
            MotionState to = from.extrapolatePos(from.getPosition()+dAccelCruise);
            MotionSegment accel = new MotionSegment(from, to);
            MotionState mid = to.forAcceleration(0);
            MotionState lastCruise = mid.extrapolatePos(to.getPosition() + dPos - dCruiseDecel);
            MotionSegment cruise = new MotionSegment(mid, lastCruise);

            MotionState decelStart = lastCruise.forAcceleration(maxDeceleration);
            MotionSegment decel = new MotionSegment(decelStart, decelStart.extrapolatePos(lastCruise.getPosition() + dCruiseDecel));
            return new MotionProfile(accel, cruise, decel);
        }
    }

    /**
     * Whether velocity should be changed first or last
     *
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
