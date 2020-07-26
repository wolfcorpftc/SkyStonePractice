package org.wolfcorp.skystone.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="Red Foundation Park Middle", group="Auto")
public class RedFoundationParkMiddle extends SkystoneAuto {
    @Override
    public void runOpMode() {
        int delay = (int) (Incrementor.delay*1000);
        sleep(delay);
        prologue();

        timer.reset();
        robot.leftServo.setPosition(0);
        robot.rightServo.setPosition(1);
        robot.open.setPosition(1);

        turnRight(1,5);
        forward(1,30);
        robot.lift.setPower(1);
        forward(0.25, 5);
        robot.lift.setPower(0.3);

        robot.leftServo.setPosition(1);
        robot.rightServo.setPosition(0);

        sleep(1000);
        robot.lift.setPower(0);
        backward(0.25,28);
        turnRight(0.25,13);
        forward(1, 15);
        robot.lift.setPower(-0.4);
        sleep(1000);
        forward(1,5);
        robot.lift.setPower(0);
        robot.leftServo.setPosition(0);
        robot.rightServo.setPosition(1);
        //sidestepRight(1,4);
        int time = (int) timer.milliseconds();
        sleep(23000 - time);

        backward(1, 5);
        turnLeft(1,13);
        forward(1, 12);
        turnLeft(1,13);
        forward(1,25);

        telemetry.addData("Path", "Complete");
        telemetry.update();
    }
}