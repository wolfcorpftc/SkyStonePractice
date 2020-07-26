package org.wolfcorp.skystone;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name="Red Foundation Park Wall", group="Auto")
public class RedFoundationParkWall extends SkystoneAuto {
    @Override
    public void runOpMode() {
        int delay = (int) (Incrementor.delay * 1000);
        sleep(delay);

        robot.init(hardwareMap);

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Status", "Resetting Encoders");    //
        telemetry.update();

        // Reset encoder and set mode
        robot.setDriveRunMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.setDriveRunMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Send telemetry message to indicate successful Encoder reset
        telemetry.addData("Path0", "Starting at %7d :%7d",
                robot.leftFront.getCurrentPosition(),
                robot.rightFront.getCurrentPosition(),
                robot.leftFront.getCurrentPosition(),
                robot.rightFront.getCurrentPosition());
        telemetry.update();
        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        timer.reset();
        robot.leftServo.setPosition(0);
        robot.rightServo.setPosition(1);
        robot.open.setPosition(1);

        turnRight(1, 5);
        forward(1, 30);
        robot.lift.setPower(1);
        forward(0.25, 5);
        robot.lift.setPower(0.3);

        robot.leftServo.setPosition(1);
        robot.rightServo.setPosition(0);
        sleep(1000);

        robot.lift.setPower(0);
        backward(0.25, 28);
        turnRight(0.25, 13);
        forward(1, 15);
        robot.lift.setPower(-0.4);
        sleep(1000);
        forward(1, 5);
        robot.lift.setPower(0);
        robot.leftServo.setPosition(0);
        robot.rightServo.setPosition(1);
        sidestepLeft(1, 15);

        int time = (int) timer.milliseconds();
        sleep(26000 - time);

        backward(1, 33);

        telemetry.addData("Path", "Complete");
        telemetry.update();
    }
}
