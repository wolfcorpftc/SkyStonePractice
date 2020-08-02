package org.wolfcorp.skystone.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvInternalCamera;
import org.wolfcorp.skystone.vision.SkystoneDetectorBase;

@Autonomous(name="Skystone Identification Practice", group="Auto")
public class SkystoneDetectTest extends SkystoneDetectMode {
    @Override
    protected void prologue() {
        // https://github.com/OpenFTC/EasyOpenCV/blob/master/examples/src/main/java/org/openftc/easyopencv/examples/InternalCameraExample.java
        // Initialize the back-facing camera
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        phoneCam = OpenCvCameraFactory.getInstance().createInternalCamera(OpenCvInternalCamera.CameraDirection.BACK, cameraMonitorViewId);
        // Connect to the camera
        phoneCam.openCameraDevice();
        // Use the SkystoneDetector pipeline
        // processFrame() will be called to process the frame
        phoneCam.setPipeline(detector);
        // Remember to change the camera rotation
        phoneCam.startStreaming(width, height, OpenCvCameraRotation.UPRIGHT);
    }

    @Override
    public void runOpMode() {
        prologue();
        String output = "";
        waitForStart();
        while (opModeIsActive()) {
            detector.resetStatistics();
            SkystoneDetectorBase.Location location = detector.getLocation();
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
            telemetry.addData("Detector was ran", detector.ran? "yes":"no");
            telemetry.addData("Empty frame", detector.empty? "yes":"no");
            telemetry.update();
            sleep(500);
        }
        //transportSkystone(location);
        phoneCam.stopStreaming();
    }

    @Override
    protected void moveToNextPair() { }

    @Override
    protected void transportSkystone(SkystoneDetectorBase.Location location) {
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
