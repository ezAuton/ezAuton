package com.team2502.ezauton.localization.sensors;

public interface ICompass extends ISensor
{
    /**
     * @return Degrees. In front of robot is 0. To left is 90, behind is 180, to right is 270, top is 0
     */
    double getDegrees();

    default double getRadians()
    {
        return (getDegrees() / 180F * Math.PI);
    }
}
