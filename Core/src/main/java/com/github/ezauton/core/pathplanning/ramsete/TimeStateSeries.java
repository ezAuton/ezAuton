package com.github.ezauton.core.pathplanning.ramsete;

import com.github.ezauton.core.pathplanning.LinearPathSegment;
import com.github.ezauton.core.pathplanning.Path;
import com.github.ezauton.core.pathplanning.PathSegment;
import com.github.ezauton.core.pathplanning.PathSegmentInterpolated;
import com.github.ezauton.core.trajectory.geometry.ImmutableVector;
import com.github.ezauton.core.utils.InterpolationMap;
import com.github.ezauton.core.utils.MathUtils;

import java.util.Iterator;

public class TimeStateSeries {
    private final Path path;

    private InterpolationMap timeStateMap_X;
    private InterpolationMap timeStateMap_Y;
    private InterpolationMap timeStateMap_THETA;
    private InterpolationMap timeStateMap_angVel;
    private InterpolationMap timeStateMap_linearVel;

    private RamseteMovementStrategy.DesiredState finalState;

    public TimeStateSeries(Path path, double dt) {
        this.path = path;
        updateInterpolationMaps(dt);
    }

    public RamseteMovementStrategy.DesiredState getDesiredPose(double time_seconds) {
        return new RamseteMovementStrategy.DesiredState(
                new RamseteMovementStrategy.Pose(
                        timeStateMap_X.get(time_seconds),
                        timeStateMap_Y.get(time_seconds),
                        timeStateMap_THETA.get(time_seconds)
                ),
                new RamseteMovementStrategy.ControlOutput(
                        timeStateMap_linearVel.get(time_seconds),
                        timeStateMap_angVel.get(time_seconds)
                ));
    }

    /**
     * Create reference states (i.e the robot should be at (x, y) location pointing in (theta) direction at (t) time)
     *
     * @param dt The timestep (in milliseconds) to use in order to generate these reference states.
     */
    void updateInterpolationMaps(double dt) {
        Iterator<PathSegmentInterpolated> pathSegments = new Iterator<PathSegmentInterpolated>() {
            Iterator<PathSegment> dumb = path.iterator();

            @Override
            public boolean hasNext() {
                return dumb.hasNext();
            }

            @Override
            public PathSegmentInterpolated next() {
                return (PathSegmentInterpolated) dumb.next();
            }
        };

        PathSegmentInterpolated currentPathSegment = pathSegments.next();
        timeStateMap_X = new InterpolationMap(0D, currentPathSegment.getFrom().get(0));
        timeStateMap_Y = new InterpolationMap(0D, currentPathSegment.getFrom().get(1));
        timeStateMap_THETA = new InterpolationMap(0D, calculateThetaOfLinearPathSegment(currentPathSegment));
        timeStateMap_linearVel = new InterpolationMap(0D, 0D);
        timeStateMap_angVel = new InterpolationMap(0D, 0D);

        ImmutableVector simulatedPosition = currentPathSegment.getFrom();

        //TODO: Support starting at a non-zero speed
        double simulatedSpeed = 0;
        double absoluteDistance = 0;
        double timer = 0;

        double lastTheta = 0;
        double theta = 0;
        while (currentPathSegment != null) {
            // Accelerate our simulated robot
            double currentAcc = 0;
            if (simulatedSpeed < currentPathSegment.getSpeed(absoluteDistance)) {
                currentAcc = currentPathSegment.getMaxAccel();
            } else if (simulatedSpeed < currentPathSegment.getSpeed(absoluteDistance)) {
                currentAcc = currentPathSegment.getMaxDecel();
            }

            // Figure out how far we went in the last loop
            double deltaAbsoluteDistance = 0.5 * currentAcc * dt * dt + simulatedSpeed * dt;

            // Update position
            simulatedSpeed += currentAcc * dt;
            absoluteDistance += deltaAbsoluteDistance;

            lastTheta = theta;
            theta = calculateThetaOfLinearPathSegment(currentPathSegment);
            ImmutableVector deltaPosition = MathUtils.Geometry.getVector(deltaAbsoluteDistance, theta);
            simulatedPosition = simulatedPosition.add(deltaPosition);

            // Record position
            timeStateMap_X.put(timer, simulatedPosition.get(0));
            timeStateMap_Y.put(timer, simulatedPosition.get(1));
            timeStateMap_THETA.put(timer, theta);
            timeStateMap_linearVel.put(timer, simulatedSpeed);
            timeStateMap_angVel.put(timer, (theta - lastTheta) / dt);


            // Progress forwards in time, space
            timer += dt;

            if (absoluteDistance >= currentPathSegment.getAbsoluteDistanceEnd()) {
                if (pathSegments.hasNext()) {
                    currentPathSegment = pathSegments.next();
                } else {
                    currentPathSegment = null;
                }
            }
        }

        finalState = getDesiredPose(timer);
    }

    /**
     * Calculate the direction in which a linear path segment is pointing
     *
     * @param segment The linear path segment
     * @return The direction it is pointing, with 0 radians being parallel to the y axis.
     */
    double calculateThetaOfLinearPathSegment(LinearPathSegment segment) {
        double dy = segment.getTo().get(1) - segment.getFrom().get(1);
        double dx = segment.getTo().get(0) - segment.getFrom().get(0);

        return Math.atan2(dy, dx) - Math.PI / 2;
    }

    public RamseteMovementStrategy.DesiredState getFinalState() {
        return finalState;
    }

    @Deprecated
        //TOOD: Remove, for testing only
    public void printCSV() {
        System.out.println("t,x,y,theta, linearvel, angularvel");
        String formatString = "%f, %f, %f, %f, %f, %f\n";

        for (double time : timeStateMap_X.keySet()) {
            System.out.printf(formatString,
                    time,
                    timeStateMap_X.get(time),
                    timeStateMap_Y.get(time),
                    timeStateMap_THETA.get(time),
                    timeStateMap_linearVel.get(time),
                    timeStateMap_angVel.get(time));
        }
    }

}
