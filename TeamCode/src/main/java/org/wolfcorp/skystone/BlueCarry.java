package org.wolfcorp.skystone;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="Blue 1 Skystone and Foundation", group="Auto")
public class BlueCarry extends SkystoneCarry {


    @Override
    public void runOpMode() {
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

        if ("Left".equals(direction)) {
            encoderDrive(0.7, -13, 13, 13, -13, 5);
        } else if ("None".equals(direction)) {
            encoderDrive(0.7, 12, -12, -12, 12, 5);
        }


        encoderDrive(0.8, 25, 25, 5);

        robot.open.setPosition(0);

        timer.reset();
        while (timer.milliseconds() < 800 && opModeIsActive());

        encoderDrive(1, -23, -23, 4);
        rotate(90,1,2500);
        robot.setDrivePower(0);
        encoderDrive(1, 40, 40, 7);
        robot.open.setPosition(1);
        encoderDrive(1, -60, -60, 7);

        timer.reset();
        while(opModeIsActive() && timer.milliseconds() < 2500) {
            resetPosition();
        }

        if(direction.equals("None"))
            encoderDrive(0.7, 10, -10, -10, 10, 5);

        encoderDrive(0.8, -10, -10, 2);
        encoderDrive(0.8, 26, 26, 5);
        robot.open.setPosition(0);

        timer.reset();
        while (timer.milliseconds() < 800 && opModeIsActive());

        encoderDrive(1, -21, -21, 4);
        rotate(90,1,2500);
        robot.setDrivePower(0);
        encoderDrive(1, 60, 60, 7);
        robot.open.setPosition(1);
        encoderDrive(1,-15,-15,4);
    }
}