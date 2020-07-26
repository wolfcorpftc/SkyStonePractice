package org.wolfcorp.skystone;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name="Blue Foundation Park Wall", group="Auto")
public class BlueFoundationParkWall extends SkystoneAuto {
    @Override
    public void runOpMode() {
        int delay = (int) (Incrementor.delay * 1000);
        sleep(delay);
        /*
         * Initialize the drive system variables.
         * The init() method of the hardware class does all the work here
         */
        robot.init(hardwareMap);

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Status", "Resetting Encoders");    //
        telemetry.update();
        //Necessary stuff

        robot.leftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.rightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.leftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.rightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        robot.leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.leftBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.rightBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


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

        turnLeft(1, 5);
        forward(1, 30);
        robot.lift.setPower(1);
        forward(0.25, 5);
        robot.lift.setPower(0.3);

        robot.leftServo.setPosition(1);

        robot.rightServo.setPosition(0);
        sleep(1000);
        robot.lift.setPower(0);
        backward(0.25, 27);
        turnLeft(0.25, 13);
        forward(1, 15);
        robot.lift.setPower(-0.4);
        sleep(1000);
        forward(1, 5);

        robot.lift.setPower(0);
        robot.leftServo.setPosition(0);
        robot.rightServo.setPosition(1);
        sidestepRight(1, 15);
        int time = (int) timer.milliseconds();
        sleep(26000 - time);

        backward(1, 33);

        telemetry.addData("Path", "Complete");


        telemetry.update();
    }
}
