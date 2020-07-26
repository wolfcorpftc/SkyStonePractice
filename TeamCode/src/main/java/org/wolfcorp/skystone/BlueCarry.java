/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.wolfcorp.skystone;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
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
import org.wolfcorp.skystone.SkystoneAuto;
import org.wolfcorp.skystone.SkystoneCarry;

import java.util.List;

@Autonomous(name="Blue 1 Skystone and Foundation", group="Auto")
public class BlueCarry extends SkystoneCarry {

    @Override
    public void runOpMode() {
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

        while (opModeIsActive() && !robot.imu.isGyroCalibrated()) {
            sleep(50);
            idle();
        }

        telemetry.addData("Mode", "Done Calibrating");
        telemetry.update();

        waitForStart();
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