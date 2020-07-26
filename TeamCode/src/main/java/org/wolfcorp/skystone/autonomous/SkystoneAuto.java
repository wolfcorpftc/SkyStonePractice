package org.wolfcorp.skystone.autonomous;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.wolfcorp.skystone.base.SkystoneOpMode;

public abstract class SkystoneAuto extends SkystoneOpMode {
    /** Initialize hardware and wait for start */
    protected void prologue() {
        /*
         * Initialize the drive system variables.
         * The init() method of the hardware class does all the work here
         */
        robot.init(hardwareMap);

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Status", "Resetting Encoders");    //
        telemetry.update();

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
    }

    /*
     *  Method to perfmorm a relative move, based on encoder counts.
     *  Encoders are not reset as the move is based on the current position.
     *  Move will stop if any of three conditions occur:
     *  1) Move gets to the desired position (unless timeout has been reached)
     *  2) Driver stops the opmode running.
     */
    public void drive(double speed,
                      double leftInches, double rightInches,
                      double leftBackInches, double rightBackInches,
                      double timeoutSec) {
        int leftTarget;
        int rightTarget;
        int leftBackTarget;
        int rightBackTarget;

        if (opModeIsActive()) {
            leftTarget = robot.leftFront.getCurrentPosition() + (int) (leftInches * COUNTS_PER_INCH);
            rightTarget = robot.rightFront.getCurrentPosition() + (int) (rightInches * COUNTS_PER_INCH);
            leftBackTarget = robot.leftBack.getCurrentPosition() + (int) (leftBackInches * COUNTS_PER_INCH);
            rightBackTarget = robot.rightBack.getCurrentPosition() + (int) (rightBackInches * COUNTS_PER_INCH);

            robot.setDriveTargetPos(
                    leftTarget,
                    rightTarget,
                    leftBackTarget,
                    rightBackTarget
            );

            robot.setDriveRunMode(DcMotor.RunMode.RUN_TO_POSITION);

            timer.reset();
            robot.setDrivePower(Math.abs(speed));

            while (opModeIsActive()
                    && (timeoutSec <= 0 || timer.seconds() < timeoutSec)
                    && robot.leftFront.isBusy()
                    && robot.rightFront.isBusy()
                    && robot.leftBack.isBusy()
                    && robot.rightBack.isBusy()) {
                telemetry.addData("Path1", "Running to %7d :%7d", leftTarget, rightTarget);
                telemetry.addData("LeftFront Current", robot.leftFront.getCurrentPosition());
                telemetry.addData("RightFront Current", robot.rightFront.getCurrentPosition());
                telemetry.addData("LeftBack Current", robot.leftBack.getCurrentPosition());
                telemetry.addData("RightBack Current", robot.rightBack.getCurrentPosition());
                telemetry.addData("Path2", "Running at %7d :%7d :%7d :%7d",
                        robot.leftFront.getCurrentPosition(),
                        robot.rightFront.getCurrentPosition(),
                        robot.leftBack.getCurrentPosition(),
                        robot.rightBack.getCurrentPosition());
                telemetry.update();
            }

            robot.setDrivePower(0);
            robot.setDriveRunMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }

    /** Same params but no timeout */
    public void drive(double speed,
                      double leftInches, double rightInches,
                      double leftBackInches, double rightBackInches) {
        drive(speed, leftInches, rightInches, leftBackInches, rightBackInches, -1);
    }

    /** Same params but no timeout nor back motor args */
    public void drive(double speed, double leftInches, double rightInches, double timeoutSec) {
        drive(speed, leftInches, rightInches, leftInches, rightInches, timeoutSec);
    }

    public void forward(double speed, double distance, double timeoutSec) {
        double converted = distance * DRIVE_CONVERSION;
        drive(speed, -converted, -converted, timeoutSec);
    }

    public void forward(double speed, double distance) {
        forward(speed, distance, -1);
    }

    public void backward(double speed, double distance, double timeoutSec) {
        double converted = distance * DRIVE_CONVERSION;
        drive(speed, converted, converted, timeoutSec);
    }

    public void backward(double speed, double distance) {
        backward(speed, distance, -1);
    }

    public void turnLeft(double speed, double degrees) {
        double converted = degrees * DEG_CONVERSION;
        drive(speed, converted, -converted, converted, -converted);
    }

    public void turnRight(double speed, double degrees) {
        double converted = degrees * DEG_CONVERSION;
        drive(speed, -converted, converted, -converted, converted);
    }

    public void sidestepRight(double speed, double distance) {
        double converted = distance * DRIVE_CONVERSION;
        drive(speed, converted, -converted, -converted, converted);
    }

    public void sidestepLeft(double speed, double distance) {
        double converted = distance * DRIVE_CONVERSION;
        drive(speed, -converted, converted, converted, -converted);
    }
}
