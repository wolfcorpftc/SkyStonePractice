package org.wolfcorp.skystone.base;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

public abstract class SkystoneOpMode extends LinearOpMode {
    /* Declare OpMode members. */
    protected SkyStoneRobot robot = new SkyStoneRobot();
    protected ElapsedTime runtime   = new ElapsedTime();
    protected ElapsedTime timer     = new ElapsedTime();

    public static final double     COUNTS_PER_MOTOR_REV    = 1120;  // For Andymark 40s
    public static final double     DRIVE_GEAR_REDUCTION    = 1.0 ;  // 1.0 because we don't have it geared up.
    public static final double     WHEEL_DIAMETER_INCHES   = 4.0 ;  // For figuring circumference
    public static final double     COUNTS_PER_INCH         =
            (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * 3.1415);
    public static final double     MAX_SPEED               = 1; //Save Time
    public static final double     DRIVE_CONVERSION        = 1;
    public static final double     DEG_CONVERSION          = 1;

    public static void normalize(double[] wheelSpeeds) {
        double maxMagnitude = Math.abs(wheelSpeeds[0]);

        for (int i = 1; i < wheelSpeeds.length; i++) {
            double magnitude = Math.abs(wheelSpeeds[i]);

            if (magnitude > maxMagnitude) {
                maxMagnitude = magnitude;
            }
        }

        if (maxMagnitude > 1.0) {
            for (int i = 0; i < wheelSpeeds.length; i++) {
                wheelSpeeds[i] /= maxMagnitude;
            }
        }
    }

}
