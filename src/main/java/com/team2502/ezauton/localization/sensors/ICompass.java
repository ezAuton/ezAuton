package com.team2502.ezauton.localization.sensors;

/**
 * A CCW rotational censor on [0,360) which is 0 when facing forward, 90 when facing west, ...
 */
//TODO: Better name?
public interface ICompass extends ISensor
{
    /**
     * @return Current robot heading in degrees. In front of robot is 0. To left is 90, behind is 180, to right is 270, top is 0
     */
    double getDegrees();

    /**
     * @return Current robot heading in radians. In front of robot is 0. To left is Math.PI / 2, behind is PI, etc . . .
     */
    default double getRadians()
    {
        return (getDegrees() / 180F * Math.PI);
    }
}
