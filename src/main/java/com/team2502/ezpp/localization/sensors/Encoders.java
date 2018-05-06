package com.team2502.ezpp.localization.sensors;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class Encoders {

    int MAG_ENCODER = 4096;

    public static IEncoder fromTalon(TalonSRX talonSRX, int unitsPerRev)
    {
        return new IEncoder() {
            @Override
            public float getPosition() {
                return (float) talonSRX.getSelectedSensorPosition(0)/unitsPerRev;
            }

            @Override
            public float getVelocity() {
                return (float) talonSRX.getSelectedSensorVelocity(0)/unitsPerRev*10F;
            }
        };
    }
}
