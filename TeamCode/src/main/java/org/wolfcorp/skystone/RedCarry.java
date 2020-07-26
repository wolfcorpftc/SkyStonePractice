package org.wolfcorp.skystone;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="Red 1 Skystone and Foundation", group="Auto")
public class RedCarry extends SkystoneCarry {
    @Override
    public void runOpMode() {
        prologue();
        resetAngle();
        timer.reset();
        while (timer.milliseconds() < 2000 && opModeIsActive()) {
            TFIdentify();
        }

        tfod.shutdown();

        telemetry.addData("Side", direction);
        telemetry.update();

        robot.open.setPosition(1);

        robot.twist.setPosition(1);

        robot.leftServo.setPosition(0);
        robot.rightServo.setPosition(1);

        if (direction.equals("Right")) {
            // default direction, do nothing
        } else if (direction.equals("Left")) {
            encoderDrive(0.7, -12, 12, 12, -12, 5);
        } else if (direction.equals("None")) {
            encoderDrive(0.7, 8, -8, -8, 8, 5);
        }

        encoderDrive(0.8, 25, 25, 5);

        robot.open.setPosition(0);

        timer.reset();
        while (timer.milliseconds() < 800 && opModeIsActive());

        encoderDrive(1, -7, -7, 42);
        rotate(-90,1,2000);
        robot.setDrivePower(0);
        encoderDrive(1, 57, 57, 7);

        robot.leftServo.setPosition(0);
        robot.rightServo.setPosition(1);

        timer.reset();
        while(opModeIsActive()&&timer.milliseconds()<2000) {
            resetPosition();
            if(timer.milliseconds()>1000){
                robot.lift.setPower(1);
            }
        }

        robot.twist.setPosition(0);

        encoderDrive(1,5,5,3);
        encoderDrive(0.25,3,3,3);

        robot.open.setPosition(1);

        robot.lift.setPower(0);

        robot.leftServo.setPosition(1);
        robot.rightServo.setPosition(0);
        sleep(1000);

        encoderDrive(0.5,3,-3,4);
        robot.leftServo.setPosition(0);
        robot.rightServo.setPosition(1);
        encoderDrive(0.25,2,2,2);
        robot.leftServo.setPosition(1);
        robot.rightServo.setPosition(0);
        sleep(1000);
        encoderDrive(0.25,-22,-22,5);
        encoderDrive(0.5,10,-10,4);
        encoderDrive(1, 25,25,4);

        robot.leftServo.setPosition(0);
        robot.rightServo.setPosition(1);

        encoderDrive(1,-7,-7,3);

        timer.reset();
        while(opModeIsActive()&&timer.milliseconds()<2000) {
            resetPosition();
            if(timer.milliseconds()>1300){
                robot.lift.setPower(-1);
            }
        }

        robot.lift.setPower(0);
        encoderDrive(1,6,6,5);
        rotate(90,1,2000);
        encoderDrive(1,40,40,7);
    }
}

