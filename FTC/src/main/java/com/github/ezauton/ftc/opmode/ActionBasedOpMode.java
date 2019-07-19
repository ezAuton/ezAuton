package com.github.ezauton.ftc.opmode;



import com.github.ezauton.core.action.Action;
import com.github.ezauton.core.action.ActionGroup;
import com.github.ezauton.core.action.tangible.MainActionScheduler;
import com.github.ezauton.core.utils.RealClock;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

public abstract class ActionBasedOpMode extends LinearOpMode {

    protected MainActionScheduler actionScheduler = new MainActionScheduler(RealClock.CLOCK);

    // TODO: fancy ezAuton hardware map?

//    public abstract Action initEz();

    protected void initEz(){}

    protected void runActions() {}

    @Override
    public final void runOpMode() throws InterruptedException {
        initEz();

        waitForStart();

        runActions();

        whenFinished();
    }

    protected void whenFinished() {};


}
