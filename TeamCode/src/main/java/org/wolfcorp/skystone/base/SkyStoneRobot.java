package org.wolfcorp.skystone.base;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

public class SkyStoneRobot
{
    /* Public OpMode members. */
    public DcMotor  leftFront   = null;
    public DcMotor  rightFront  = null;
    public DcMotor  leftBack     = null;
    public DcMotor  rightBack     = null;
    public DcMotor  lift        = null;

    public Servo    leftServo     = null;
    public Servo    rightServo    = null;
    public Servo    twist       = null;
    public Servo    open        = null;
    public Servo    capstone = null;

    public BNO055IMU imu;

    /* local OpMode members. */
    HardwareMap hwMap           = null;
    private ElapsedTime period  = new ElapsedTime();

    /* Constructor */
    public SkyStoneRobot() {}

    /* Initialize standard Hardware interfaces */
    public void init(HardwareMap ahwMap) {
        // Save reference to Hardware map
        hwMap = ahwMap;

        // Define and Initialize Motors
        leftFront  = hwMap.get(DcMotor.class, "leftFront");
        rightFront = hwMap.get(DcMotor.class, "rightFront");
        leftBack   = hwMap.get(DcMotor.class, "leftBack");
        rightBack  = hwMap.get(DcMotor.class, "rightBack");

        lift       = hwMap.get(DcMotor.class, "lift");

        leftServo  = hwMap.get(Servo.class, "leftServo");
        rightServo = hwMap.get(Servo.class, "rightServo");
        twist      = hwMap.get(Servo.class, "twist");
        open       = hwMap.get(Servo.class, "open");
        capstone    = hwMap.get(Servo.class, "capstone");

        // Set motor direction
        leftFront.setDirection(DcMotor.Direction.REVERSE); // Set to REVERSE if using AndyMark motors
        rightFront.setDirection(DcMotor.Direction.FORWARD);// Set to FORWARD if using AndyMark motors
        leftBack.setDirection(DcMotor.Direction.REVERSE); // Set to REVERSE if using AndyMark motors
        rightBack.setDirection(DcMotor.Direction.FORWARD);// Set to FORWARD if using AndyMark motors
        lift.setDirection(DcMotor.Direction.FORWARD);

        // Set all motors to zero power
        setDrivePower(0);
        lift.setPower(0);

        // Configure motors
        setDriveRunMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        capstone.setPosition(0.5);
    }

    public void setDrivePower(double power) {
        setDrivePower(power, power, power, power);
    }
    public void setDrivePower(double leftPower, double rightPower) {
        setDrivePower(leftPower, rightPower, leftPower, rightPower);
    }
    public void setDrivePower(double leftPower, double rightPower,
                              double leftBackPower, double rightBackPower) {
        leftFront.setPower(leftPower);
        rightFront.setPower(rightPower);
        leftBack.setPower(leftBackPower);
        rightBack.setPower(rightBackPower);
    }

    public void setDriveTargetPos(int leftPos, int rightPos,
                                  int leftBackPos, int rightBackPos) {
        leftFront.setTargetPosition(leftPos);
        rightFront.setTargetPosition(rightPos);
        leftBack.setTargetPosition(leftBackPos);
        rightBack.setTargetPosition(rightBackPos);
    }
    public void setDriveRunMode(DcMotor.RunMode runMode) {
        leftFront.setMode(runMode);
        rightFront.setMode(runMode);
        leftBack.setMode(runMode);
        rightBack.setMode(runMode);
    }

    public void gripperUp() {
        leftServo.setPosition(0);
        rightServo.setPosition(1);
    }
    public void gripperDown() {
        leftServo.setPosition(1);
        rightServo.setPosition(0);
    }

    public void snapClaw() {
        double[] positions = {0, 0.9};
        double closest = 0;
        double distance = 1;
        for (double position : positions) {
            if (Math.abs(twist.getPosition() - position) < distance) {
                distance = Math.abs(twist.getPosition() - position);
                closest = position;
            }
        }
        twist.setPosition(closest);
    }
    public void openClaw() {
        open.setPosition(1);
    }
    public void closeClaw() {
        open.setPosition(0);
    }
}