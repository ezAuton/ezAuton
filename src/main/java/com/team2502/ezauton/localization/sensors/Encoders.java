package com.team2502.ezauton.localization.sensors;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.team2502.ezauton.utils.IStopwatch;

public class Encoders {

    int CTRE_MAG_ENCODER = 4096;

    public static IEncoder fromTalon(TalonSRX talonSRX, int unitsPerRev)
    {
        return () -> talonSRX.getSelectedSensorPosition(0)/unitsPerRev;
    }

    public static IEncoder fromTachometer(ITachometer tachometer, IStopwatch stopwatch)
    {
        return new IEncoder() {

            double position = 0;

            @Override
            public double getPosition() {
                position+=stopwatch.pop()*tachometer.getVelocity();
                return position;
            }
        };
    }
}
