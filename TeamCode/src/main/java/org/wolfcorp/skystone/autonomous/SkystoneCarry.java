package org.wolfcorp.skystone.autonomous;

import android.annotation.SuppressLint;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.List;

public abstract class SkystoneCarry extends SkystoneAuto {
    protected Orientation lastAngles = new Orientation();

    protected double globalAngle;
    protected String direction = "None";

    protected ElapsedTime timer = new ElapsedTime();
    protected static final String TFOD_MODEL_ASSET = "Skystone.tflite";
    protected static final String LABEL_FIRST_ELEMENT = "Stone";
    protected static final String LABEL_SECOND_ELEMENT = "Skystone";

    protected static final String VUFORIA_KEY =
            "AVzVxzP/////AAAAGU4AhEEomUHnkh2yZktVPG8DmuBSQl4d6sYbJET+5UjFj8ZhJLWO63TqoiJDyEbrKR0DjSwIgpSGQDyyxIAWqOlieLoRMu5Aw2nRVudpi7XxSyiLz9jFDP1y/TGAleOq/pnjdq8h1KdXyUxwN9JcSBWMLujGdLbXRXzzOxlbrG6vesEXkSXOk77mkXy/TAFYu3pDpPIDa6Hywla3p+cTk+SZ+ztzcXwQ599/qWPLOQvDH35iO/jYkO0yY7aHPxf4n5WtkuUWCwNVcUuesxqQSV0eXpVy260b6JEARlLp6VVwaeEsBvU8U3L6Wx2S+52Va+JYXUOj6SIyWeeyF072U4F+IIrOVbdHjawJVwgB11T1";

    protected VuforiaLocalizer vuforia;
    protected TFObjectDetector tfod;

    @Override
    protected void prologue() {
        robot.init(hardwareMap);
        robot.setDriveRunMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.setDriveRunMode(DcMotor.RunMode.RUN_USING_ENCODER);

        if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
            initTFOD();

            /* Activate TensorFlow Object Detection before we wait for the start command.
             * Do it here so that the Camera Stream window will have the TensorFlow annotations visible.
             */
            if (tfod != null) {
                tfod.activate();
            }
        } else {
            telemetry.addData("Sorry!", "This device is not compatible with TFOD");
            initVuforia();
        }

        telemetry.addData("Mode", "calibrating...");
        telemetry.update();

        while (opModeIsActive() && !robot.imu.isGyroCalibrated());

        telemetry.addData("Mode", "Done Calibrating");
        telemetry.update();

        waitForStart();
    }

    //all four encoders drive procedure
    protected void resetAngle() {
        lastAngles = robot.imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        globalAngle = 0;
    }

    // Checking angle using gyro
    protected double getAngle() {
        // We experimentally determined the Z axis is the axis we want to use for heading angle.
        // We have to process the angle because the imu works in euler angles so the Z axis is
        // returned as 0 to +180 or 0 to -180 rolling back to -179 or +179 when rotation passes
        // 180 degrees. We detect this transition and track the total cumulative angle of rotation.

        Orientation angles = robot.imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        double deltaAngle = angles.firstAngle - lastAngles.firstAngle;

        if (deltaAngle < -180)
            deltaAngle += 360;
        else if (deltaAngle > 180)
            deltaAngle -= 360;

        globalAngle += deltaAngle;
        lastAngles = angles;

        return globalAngle;
    }

    protected double getCorrection() {
        // The gain value determines how sensitive the correction is to direction changes.
        // You will have to experiment with your robot to get small smooth direction changes
        // to stay on a straight line.
        double correction;
        double angle = getAngle();
        double gain = .10;

        if (angle == 0) correction = 0;             // no adjustment.
        else correction = -angle;        // reverse sign of angle for correction.
        correction = correction * gain / 2;

        if (correction < 0.4 && correction > 0.1) {
            correction = 0.4;
        }
        if (correction > -0.4 && correction < -0.1) {
            correction = -0.4;
        }
        if (-0.1 <= correction && correction <= 0.1) {
            correction = 0;
        }

        return correction;
    }

    //Turning position to original direction using gyro
    protected void resetPosition() {
        double correction = getCorrection() / 2;

        telemetry.addData("1 imu heading", lastAngles.firstAngle);
        telemetry.addData("2 global heading", globalAngle);
        telemetry.addData("3 correction", correction);
        telemetry.update();

        robot.setDrivePower(0 - correction, 0 + correction);
    }

    protected void rotate(int degrees, double power, int timeoutMillis) {
        double leftPower, rightPower;

        // getAngle() returns + when rotating counter clockwise (left) and - when rotating
        // clockwise (right).
        timer.reset();
        while (opModeIsActive() && timer.milliseconds() < timeoutMillis) {
            if (degrees < getAngle()) {
                leftPower = Math.min(power, Math.abs(degrees - getAngle()) / 30);
                rightPower = -leftPower;
            } else if (degrees > getAngle()) {
                leftPower = -Math.min(power, Math.abs(degrees - getAngle()) / 30);
                rightPower = -leftPower;
            } else {
                leftPower = 0;
                rightPower = 0;
            }

            // set power to rotate.
            robot.setDrivePower(leftPower, rightPower);
        }

        // turn the motors off.
        robot.setDrivePower(0);
    }

    protected void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the TensorFlow Object Detection engine.
    }

    /**
     * Initialize the TensorFlow Object Detection engine.
     */
    protected void initTFOD() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minimumConfidence = 0.7;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_FIRST_ELEMENT, LABEL_SECOND_ELEMENT);
    }

    @SuppressLint("DefaultLocale")
    protected void identifySkystone() {
        if (tfod != null) {
            // getUpdatedRecognitions() will return null if no new information is available since
            // the last time that call was made.
            List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
            if (updatedRecognitions != null) {
                telemetry.addData("# Object Detected", updatedRecognitions.size());

                // step through the list of recognitions and display boundary info.
                int i = 0;
                boolean skystoneExists = false;
                for (Recognition recognition : updatedRecognitions) {
                    telemetry.addData(String.format("label (%d)", i), recognition.getLabel());
                    telemetry.addData(String.format("  left,top (%d)", i), "%.03f , %.03f",
                            recognition.getLeft(), recognition.getTop());
                    telemetry.addData(String.format("  right,bottom (%d)", i), "%.03f , %.03f",
                            recognition.getRight(), recognition.getBottom());
                    double centerX = (recognition.getLeft() + recognition.getRight()) / 2;
                    if (recognition.getLabel().equals("Skystone") && centerX < 350) {
                        direction = "Left";
                        skystoneExists = true;
                    } else if (recognition.getLabel().equals("Skystone") && centerX >= 350) {
                        direction = "Right";
                        skystoneExists = true;
                    }
                }
                if(!skystoneExists){
                    direction = "None";
                }
                telemetry.addData("Side", direction);
                telemetry.update();
            }
        }
    }
}
