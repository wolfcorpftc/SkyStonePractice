package org.wolfcorp.skystone.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="Red 1 Skystone and Foundation", group="Auto")
public class RedCarry extends SkystoneCarry {
    @Override
    public void runOpMode() {
        prologue();
        resetAngle();
        timer.reset();
        while (timer.milliseconds() < 2000 && opModeIsActive()) {
            identifySkystone();
        }

        tfod.shutdown();

        telemetry.addData("Side", direction);
        telemetry.update();

        robot.openClaw();

        robot.twist.setPosition(1);

        robot.gripperUp();

        if (direction.equals("Left")) {
            drive(0.7, -12, 12, 12, -12, 5);
        } else if (direction.equals("None")) {
            drive(0.7, 8, -8, -8, 8, 5);
        }

        drive(0.8, 25, 25, 5);
        robot.closeClaw();

        timer.reset();
        while (timer.milliseconds() < 800 && opModeIsActive());

        forward(1, 7, 42);
        rotate(-90,1,2000);
        robot.setDrivePower(0);
        backward(1, 57, 7);

        robot.gripperUp();

        timer.reset();
        while(opModeIsActive() && timer.milliseconds()<2000) {
            resetPosition();
            if(timer.milliseconds()>1000){
                robot.lift.setPower(1);
            }
        }

        robot.twist.setPosition(0);

        backward(1, 5, 3);
        backward(0.25, 3, 3);

        robot.openClaw();
        robot.open.setPosition(1);

        robot.lift.setPower(0);

        robot.gripperDown();
        sleep(1000);

        drive(0.5,3,-3,4);
        robot.gripperUp();
        backward(0.25,2,2);
        robot.gripperDown();
        sleep(1000);
        forward(0.25, 22, 5);
        drive(0.5,10,-10,4);
        backward(1, 25, 4);

        robot.gripperUp();
        forward(1,7,3);

        timer.reset();
        while(opModeIsActive() && timer.milliseconds() < 2000) {
            resetPosition();
            if(timer.milliseconds()>1300){
                robot.lift.setPower(-1);
            }
        }

        robot.lift.setPower(0);
        backward(1,6,5);
        rotate(90,1,2000);
        backward(1, 40, 7);
    }
}

