package org.wolfcorp.skystone.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvInternalCamera;
import org.wolfcorp.skystone.vision.SkystoneDetectorBase;
import org.wolfcorp.skystone.vision.SkystoneOpenCVDetector;

@Autonomous(name="Auto: SkyStone Detector", group="Auto")
public abstract class SkystoneDetectMode extends SkystoneAuto {
    int width = 320;
    int height = 240;
    // Store as variable here so we can access the location
    SkystoneOpenCVDetector detector = new SkystoneOpenCVDetector();
    // The pair of stones we scanned
    // 3 being the outermost, 1 being the innermost
    int lastPair = 3;
    OpenCvCamera phoneCam;

    // prologue() comes from our custom SkystoneAuto class
    // which inherits LinearOpMode
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
        // Change the camera rotation
        phoneCam.startStreaming(width, height, OpenCvCameraRotation.UPRIGHT);
        super.prologue(); // initialize hardware; wait for start
    }

    @Override
    public void runOpMode() {
        prologue();

        // Start from the outermost pair
        SkystoneDetectorBase.Location location = detector.getLocation();
        if (location == SkystoneDetectorBase.Location.NOT_FOUND) {
            // This means that the skystone must be
            // to the left of the current pair
            moveToNextPair();
            // The right one of the middle pair
            transportSkystone(SkystoneDetectorBase.Location.RIGHT);
            returnToNextPair();
            // The left one of the innermost pair
            transportSkystone(SkystoneDetectorBase.Location.LEFT);
        } else {
            SkystoneDetectorBase.Location opposite =
                    location == SkystoneDetectorBase.Location.LEFT?
                            SkystoneDetectorBase.Location.RIGHT:
                            SkystoneDetectorBase.Location.LEFT;
            transportSkystone(location);
            returnToNextPair();
            moveToNextPair();
            transportSkystone(opposite);
        }
        park();
        phoneCam.stopStreaming();
    }

    // methods to be defined by the Red & Blue Opmodes

    // move to next pair and update lastPair
    protected abstract void moveToNextPair();

    // grab the stone and transfer to foundation
    protected abstract void transportSkystone(SkystoneDetectorBase.Location location);

    // moves to last pair and moveToNextPair()
    protected abstract void returnToNextPair();

    // park to tape from the foundation
    protected abstract void park();
}
