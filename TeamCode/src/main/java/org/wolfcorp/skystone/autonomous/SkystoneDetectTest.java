package org.wolfcorp.skystone.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.wolfcorp.skystone.vision.SkystoneDetector;

@Autonomous(name="Skystone Identification Practice", group="Auto")
public class SkystoneDetectTest extends SkystoneDetectMode {
    @Override
    public void runOpMode() {
        String output;
        waitForStart();
        while (opModeIsActive()) {
            SkystoneDetector.SkystoneLocation location = detector.getLocation();
            switch (location) {
                case LEFT:
                    output = "Left";
                    break;
                case RIGHT:
                    output = "Right";
                    break;
                default:
                    output = "None";
            }
            telemetry.addData("Skystone Location", output);
            telemetry.update();
            sleep(500);
        }
        //transportSkystone(location);
        phoneCam.stopStreaming();
    }

    @Override
    protected void moveToNextPair() { }

    @Override
    protected void transportSkystone(SkystoneDetector.SkystoneLocation location) {
        switch (location) {
            case LEFT:
                sidestepLeft(0.5, 4);
                break;
            case RIGHT:
                sidestepRight(0.5, 4);
        }
    }

    @Override
    protected void returnToNextPair() { }

    @Override
    protected void park() { }
}
