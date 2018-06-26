package com.team2502.ezauton.localization.sensors;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.team2502.ezauton.utils.IStopwatch;

public class Encoders
{

    public static final int CTRE_MAG_ENCODER = 4096;

    public static IEncoder fromTalon(TalonSRX talonSRX, int unitsPerRev)
    {
        IEncoder encoder = new IEncoder()
        {
            @Override
            public double getPosition()
            {
                return (double) talonSRX.getSelectedSensorPosition(0);
            }

            @Override
            public double getVelocity()
            {
                return 10D * talonSRX.getSelectedSensorVelocity(0);
            }
        };
        return fixRegEncoder(encoder, unitsPerRev);
    }

    public static IEncoder fromVictor(VictorSPX victorSPX, int unitsPerRev)
    {
        IEncoder encoder = new IEncoder()
        {
            @Override
            public double getPosition()
            {
                return (double) victorSPX.getSelectedSensorPosition(0);
            }

            @Override
            public double getVelocity()
            {
                return 10D * victorSPX.getSelectedSensorVelocity(0);
            }
        };
        return fixRegEncoder(encoder, unitsPerRev);
    }

    private static IEncoder fixRegEncoder(IEncoder hardwareEncoder, int unitsPerRev)
    {
        if(unitsPerRev <= 0)
        {
            throw new IllegalArgumentException("unitsPerRev must be non-zero");
        }
        return new IEncoder()
        {
            @Override
            public double getPosition()
            {
                return hardwareEncoder.getPosition() / unitsPerRev;
            }

            @Override
            public double getVelocity()
            {
                // multiplying by 10 because velocity is in per 100ms by default
                return 10D * hardwareEncoder.getVelocity() / unitsPerRev;
            }
        };
    }

    public static IEncoder fromTachometer(ITachometer tachometer, IStopwatch stopwatch)
    {
        return new IEncoder()
        {

            double position = 0;

            @Override
            public double getVelocity()
            {
                return tachometer.getVelocity();
            }

            @Override
            public double getPosition()
            {
                position += stopwatch.pop() * tachometer.getVelocity();
                return position;
            }
        };
    }
}
