package com.team2502.ezauton.localization.sensors;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.team2502.ezauton.utils.IStopwatch;

/**
 * A utility class to convert WPILib libraries (i.e. TalonSRX, VictorSPX) into more abstract
 * representations which are used in ezAuton.
 */
public class Encoders
{

    public static final int CTRE_MAG_ENCODER = 4096;

    /**
     * Create an IEncoder from a TalonSRX
     * @param talonSRX A reference to the talon
     * @param unitsPerRev The number of feet/meters/whatever travelled per revolution of the encoder
     * @return A reference to an IEncoder
     */
    public static IEncoder fromTalon(TalonSRX talonSRX, int unitsPerRev) //TODO: Be able to handle cases when the talon has multiple sensors attached, so the selected sensor is not always the encoder
    {
        //TODO: Suggestion -- Subclass TalonSRX and implement IEncoder with it
        //TODO: Make the parameter use BaseMotorController to reduce redundancy
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

    /**
     * Create an IEncoder from a TalonSRX
     * @param victorSPX A reference to the talon
     * @param unitsPerRev The number of feet/meters/whatever travelled per revolution of the encoder
     * @return A reference to an IEncoder
     */
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

    /**
     * Have an encoder which is the average of two other encoders. If the robot is a differential drive robot and a and b
     * are two encoders on two different wheels, average(a,b) will return an encoder which will act as if it is in the
     * center of the robot and should be proportional to the tangential velocity of the robot.
     *
     * @param a An encoder on one side of the robot
     * @param b An encoder on the other side of the robot
     * @return An encoder that returns the average result of the 2 other encoders
     */
    public static IEncoder average(IEncoder a, IEncoder b)
    {
        return new IEncoder()
        {
            @Override
            public double getPosition()
            {
                return (a.getPosition() + b.getPosition()) / 2D;
            }

            @Override
            public double getVelocity()
            {
                return (a.getVelocity() + b.getVelocity()) / 2D;
            }
        };
    }

    /**
     * Make an IEncoder return distance units rather than revolutions
     * @param hardwareEncoder A reference to the IEncoder to fix
     * @param unitsPerRev     The number of distance units travelled per revolution
     * @return                An IEncoder that returns distance units, not revolutions
     */
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
                //TODO: This is not necessarily the case for non-CTRE motor controllers (see comment above for context)
                return 10D * hardwareEncoder.getVelocity() / unitsPerRev;
            }
        };
    }

    /**
     * Create an encoder from a tachometer, which can only measure speed
     *
     * @param tachometer A reference to the ITachometer
     * @param stopwatch  A device with which to measure time, either real or simulated
     * @return An IEncoder
     */
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
