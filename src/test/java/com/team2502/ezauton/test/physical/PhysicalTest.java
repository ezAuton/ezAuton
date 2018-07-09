package com.team2502.ezauton.test.physical;

import com.team2502.ezauton.actuators.IVoltageMotor;
import com.team2502.ezauton.command.IAction;
import com.team2502.ezauton.command.TimedAction;

public class PhysicalTest
{
    /**
     * Test
     * @param a
     * @param b
     */
    public static IAction test(IVoltageMotor a, IVoltageMotor b, double voltage)
    {
        return new TimedAction(20)
        {
            @Override
            public void execute()
            {
                a.runVoltage(voltage);
                b.runVoltage(voltage);
            }
        };
    }
}
