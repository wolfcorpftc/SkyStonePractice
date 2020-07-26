package org.wolfcorp.skystone;

import com.qualcomm.robotcore.hardware.DcMotor;

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
     *  1) Move gets to the desired position
     *  2) Driver stops the opmode running.
     */
    public void encoderDrive(double speed,
                             double leftInches, double rightInches,
                             double leftBackInches, double rightBackInches) {
        int newLeftTarget;
        int newRightTarget;
        int newLeftBackTarget;
        int newRightBackTarget;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newLeftTarget = robot.leftFront.getCurrentPosition() + (int)(leftInches * COUNTS_PER_INCH);
            newLeftBackTarget = robot.leftBack.getCurrentPosition() + (int)(leftBackInches * COUNTS_PER_INCH);
            newRightTarget = robot.rightFront.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);
            newRightBackTarget = robot.rightBack.getCurrentPosition() + (int)(rightBackInches * COUNTS_PER_INCH);

            robot.leftFront.setTargetPosition(newLeftTarget);
            robot.rightFront.setTargetPosition(newRightTarget);
            robot.leftBack.setTargetPosition(newLeftBackTarget);
            robot.rightBack.setTargetPosition(newRightBackTarget);


            // Turn On RUN_TO_POSITION
            robot.setDriveRunMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            runtime.reset();
            robot.setDrivePower(Math.abs(speed));

            // keep looping while we are still active, and there is time left, and both motors are running.
            // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
            // its target position, the motion will stop.  This is "safer" in the event that the robot will
            // always end the motion as soon as possible.
            // However, if you require that BOTH motors have finished their moves before the robot continues
            // onto the next step, use (isBusy() || isBusy()) in the loop test.
            while (opModeIsActive()
                    && robot.leftFront.isBusy()
                    && robot.rightFront.isBusy()
                    && robot.leftBack.isBusy()
                    && robot.rightBack.isBusy()) {

                // Display it for the driver.
                telemetry.addData("Path1",  "Running to %7d :%7d ", newLeftTarget,  newRightTarget, newLeftBackTarget, newRightBackTarget);
                telemetry.addData("Path2",  "Running at %7d :%7d",
                        robot.leftFront.getCurrentPosition(),
                        robot.rightBack.getCurrentPosition(),
                        robot.leftBack.getCurrentPosition(),
                        robot.rightFront.getCurrentPosition());
                telemetry.update();
            }

            // Stop all motion;
            robot.setDrivePower(0);

            // Turn off RUN_TO_POSITION
            robot.setDriveRunMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }


    public void encoderDrive(double speed,
                             double leftInches, double rightInches,
                             double leftBackInches, double rightBackInches,
                             double timeoutS) {
        int newLeftTarget;
        int newRightTarget;
        int newLeftBackTarget;
        int newRightBackTarget;

        if (opModeIsActive()) {

            newLeftTarget = robot.leftFront.getCurrentPosition() + (int) (leftInches * COUNTS_PER_INCH);
            newRightTarget = robot.rightFront.getCurrentPosition() + (int) (rightInches * COUNTS_PER_INCH);
            newLeftBackTarget = robot.leftBack.getCurrentPosition() + (int) (leftBackInches * COUNTS_PER_INCH);
            newRightBackTarget = robot.rightBack.getCurrentPosition() + (int) (rightBackInches * COUNTS_PER_INCH);

            robot.leftFront.setTargetPosition(newLeftTarget);
            robot.rightFront.setTargetPosition(newRightTarget);
            robot.leftBack.setTargetPosition(newLeftBackTarget);
            robot.rightBack.setTargetPosition(newRightBackTarget);

            robot.setDriveRunMode(DcMotor.RunMode.RUN_TO_POSITION);

            timer.reset();
            robot.setDrivePower(Math.abs(speed));

            while (opModeIsActive()
                    && timer.seconds() < timeoutS
                    && robot.leftFront.isBusy()
                    && robot.rightFront.isBusy()
                    && robot.leftBack.isBusy()
                    && robot.rightBack.isBusy()) {

                telemetry.addData("Path1", "Running to %7d :%7d", newLeftTarget, newRightTarget);
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

    public void forward(double speed, double distance) {
        double converted = distance * DRIVE_CONVERSION;
        encoderDrive(speed, -converted, -converted, -converted, -converted);
    }

    public void backward(double speed, double distance) {
        double converted = distance * DRIVE_CONVERSION;
        encoderDrive(speed, converted, converted, converted, converted);
    }

    public void turnLeft(double speed, double degrees) {
        double converted = degrees * DEG_CONVERSION;
        encoderDrive(speed, converted, -converted, converted, -converted);
    }

    public void turnRight(double speed, double degrees) {
        double converted = degrees * DEG_CONVERSION;
        encoderDrive(speed, -converted, converted, -converted, converted);
    }

    public void sidestepRight(double speed, double distance) {
        double converted = distance * DRIVE_CONVERSION;
        encoderDrive(speed, converted, -converted, -converted, converted);
    }

    public void sidestepLeft(double speed, double distance) {
        double converted = distance * DRIVE_CONVERSION;
        encoderDrive(speed, -converted, converted, converted, -converted);
    }
}
