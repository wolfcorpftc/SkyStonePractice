package org.wolfcorp.skystone.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.wolfcorp.skystone.base.SkyStoneRobot;
import org.wolfcorp.skystone.base.SkystoneOpMode;

@TeleOp(name="Skystone TeleOp", group="Pushbot")
public class SkystoneTeleOp extends SkystoneOpMode {

    /* Declare OpMode members. */
    SkyStoneRobot robot       = new SkyStoneRobot();   // Use a Pushbot's hardware
    double slowDown = 1;
    double spoolFast = 0.5;

    @Override
    public void runOpMode() {
        robot.init(hardwareMap);

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Message", "Hello Driver");
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();

        robot.capstone.setPosition(1);

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            // Drivetrain
            if(gamepad1.y) {
                robot.setDrivePower(0.15);
            } else if(gamepad1.a && !gamepad1.start){
                robot.setDrivePower(-0.3);
            } else {
                mecanumDrive(-gamepad1.right_stick_y,
                        gamepad1.right_stick_x,
                        gamepad1.left_stick_x * .9);
            }

            // Drivetrain speeds
            if (gamepad1.left_bumper && gamepad1.right_bumper) {
                slowDown = 1;
            } else if (gamepad1.left_bumper) {
                slowDown = 0.4;
            } else if (gamepad1.right_bumper) {
                slowDown = 0.5;
            } else {
                slowDown = 0.85;
            }

            // Lift spools
            if (gamepad2.right_stick_button) {
                spoolFast = 1;
            } else {
                spoolFast = 0.5;
            }

            if (gamepad2.y) {
                robot.lift.setPower(1);
            } else if (gamepad2.a && robot.lift.getCurrentPosition() > 0) {
                robot.lift.setPower(-spoolFast);
            } else {
                robot.lift.setPower(0);
            }

            // Claw turn
            if (gamepad2.left_stick_x > 0) {
                robot.twist.setPosition(robot.twist.getPosition() - 0.008);
                if (robot.twist.getPosition() < 0) {
                    robot.twist.setPosition(0);
                }
            } else if (gamepad2.left_stick_x < 0) {
                robot.twist.setPosition(robot.twist.getPosition() + 0.008);
                if (robot.twist.getPosition() > 0.9) {
                    robot.twist.setPosition(0.9);
                }
            }

            // Claw snap in
            if (gamepad2.left_stick_button) {
                robot.snapClaw();
            }

            // Claw open
            if (gamepad2.x) {
                robot.open.setPosition(1);
            } else if (gamepad2.b && !gamepad2.start) {
                robot.open.setPosition(0);
            }

            // Foundation grippers
            if (gamepad1.dpad_up) {
                robot.gripperUp();
            } else if (gamepad1.dpad_down) {
                robot.gripperDown();
            }

            // Capstone drop
            if (gamepad2.dpad_right) {
                robot.capstone.setPosition(0.35);
            } else if (gamepad2.dpad_left) {
                robot.capstone.setPosition(1);
            }
        }
    }

    public void mecanumDrive(double x, double y, double rotation) {
        double[] wheelSpeeds = new double[4];

        wheelSpeeds[0] = x + y + rotation;
        wheelSpeeds[1] = x - y - rotation;
        wheelSpeeds[2] = x - y + rotation;
        wheelSpeeds[3] = x + y - rotation;

        normalize(wheelSpeeds);

        wheelSpeeds[0] = Math.pow(wheelSpeeds[0],3);
        wheelSpeeds[1] = Math.pow(wheelSpeeds[1],3);
        wheelSpeeds[2] = Math.pow(wheelSpeeds[2],3);
        wheelSpeeds[3] = Math.pow(wheelSpeeds[3],3);

        normalize(wheelSpeeds);

        robot.leftFront.setPower(wheelSpeeds[0] * slowDown);
        robot.rightFront.setPower(wheelSpeeds[1] * slowDown);
        robot.leftBack.setPower(wheelSpeeds[2] * slowDown);
        robot.rightBack.setPower(wheelSpeeds[3] * slowDown);
    }
}