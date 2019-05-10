package com.github.ezauton.core.pathplanning.ramsete;

import com.github.ezauton.core.pathplanning.LinearPathSegment;
import com.github.ezauton.core.pathplanning.purepursuit.PPWaypoint;
import com.github.ezauton.core.pathplanning.purepursuit.SplinePPWaypoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RamseteTest {
    private NonLinearMovementStrategy ramsete;

    @BeforeEach
    public void init() {
        ramsete = new NonLinearMovementStrategy(0.5, 0.5, null);
    }

    @Test
    public void testAngleFinder() {
        for (double i = -10; i < 10; i++) {
            if (i == 0) continue;

            LinearPathSegment segment = generatePathSegmentAtAngle(Math.PI / i);
            System.out.println("segment.getTo() = " + segment.getTo());
            assertEquals((Math.PI / i) % Math.PI, (ramsete.calculateThetaOfLinearPathSegment(segment)) % Math.PI, 1e-5);
        }
    }

    @Test
    public void testGeneratingGoalStates() {
        ramsete = new NonLinearMovementStrategy(0.5, 0.5,
                new SplinePPWaypoint.Builder()
                        .add(0, 0, 0, 10, 10, -10)
                        .add(10, 10, 0, 10, 10, -10)
                        .buildPathGenerator().generate(0.05));
        ramsete.updateInterpolationMaps(1e-3); //1 ms

        ramsete.printCSV();
    }

    private LinearPathSegment generatePathSegmentAtAngle(double theta) {
        return (LinearPathSegment) new PPWaypoint.Builder().add(0, 0, 0, 0, 0)
                .add(-Math.sin(theta), Math.cos(theta), 0, 0, 0)
                .buildPathGenerator().generate(0.05).getPathSegments().get(0);
    }
}
