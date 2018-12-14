package org.github.ezauton.ezauton.wpilib.motors;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.SpeedController;
import org.github.ezauton.ezauton.localization.sensors.IEncoder;

import java.util.Arrays;


public class MotorControllers
{
    public static ITypicalMotor fromCTRE(BaseMotorController motorController, int pidIdx)
    {
        return new ITypicalMotor()
        {
            @Override
            public void runVoltage(double targetVoltage)
            {
                motorController.set(ControlMode.PercentOutput, targetVoltage);
            }

            @Override
            public void runVelocity(double targetVelocity)
            {
                motorController.set(ControlMode.Velocity, targetVelocity);
            }

            @Override
            public double getPosition()
            {
                return motorController.getSelectedSensorPosition(pidIdx);
            }

            @Override
            public double getVelocity()
            {
                return motorController.getSelectedSensorVelocity(pidIdx);
            }
        };
    }

    public static ITypicalMotor fromSeveralCTRE(BaseMotorController master, int pidIdx, BaseMotorController... slaves)
    {
        return new ITypicalMotor()
        {
            @Override
            public void runVelocity(double targetVelocity)
            {
                makeSlavesFollowMaster();
                master.set(ControlMode.Velocity, targetVelocity);
            }

            @Override
            public void runVoltage(double targetVoltage)
            {
                makeSlavesFollowMaster();
                master.set(ControlMode.PercentOutput, targetVoltage);
            }

            @Override
            public double getPosition()
            {
                return master.getSelectedSensorPosition(pidIdx);
            }

            @Override
            public double getVelocity()
            {
                return master.getSelectedSensorVelocity(pidIdx);
            }

            private void makeSlavesFollowMaster()
            {
                Arrays.stream(slaves).forEach(s -> s.follow(master));
            }

        };
    }

    public static IEncoder fromWPILibEncoder(Encoder encoder)
    {
        return new IEncoder()
        {
            @Override
            public double getPosition()
            {
                return encoder.getDistance();
            }

            @Override
            public double getVelocity()
            {
                return encoder.getRate();
            }
        };
    }


    public static ITypicalMotor fromPWM(PIDController controller, Encoder encoder, SpeedController motor)
    {
        return new ITypicalMotor()
        {
            @Override
            public void runVoltage(double targetVoltage)
            {
                controller.disable();
                motor.set(targetVoltage);
            }

            @Override
            public void runVelocity(double targetVelocity)
            {
                if(!controller.isEnabled())
                {
                    controller.enable();
                }
                controller.setSetpoint(targetVelocity);
            }

            @Override
            public double getPosition()
            {
                return encoder.getDistance();
            }

            @Override
            public double getVelocity()
            {
                return encoder.getRate();
            }
        };
    }
}
