package org.wolfcorp.skystone.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcontroller.external.samples.HardwarePushbot;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvInternalCamera;
import org.wolfcorp.skystone.vision.SimpleSkystoneOpenCVDetector;
import org.wolfcorp.skystone.vision.SkystoneDetectorBase;

// Now that we have a detector pipeline, we can use it to create
// an autonomous opmode
@Autonomous(name="Detect skystone", group="Auto")
public class SkystoneAutoTutorial extends LinearOpMode {
    @Override
    public void runOpMode() {
        // https://github.com/OpenFTC/EasyOpenCV/blob/master/examples/src/main/java/org/openftc/easyopencv/examples/InternalCameraExample.java
        // Initialize the back-facing camera
        int cameraMonitorViewId = hardwareMap.appContext.getResources()
                .getIdentifier("cameraMonitorViewId",
                        "id", hardwareMap.appContext.getPackageName());
        OpenCvCamera phoneCam = OpenCvCameraFactory.getInstance()
                .createInternalCamera(OpenCvInternalCamera.CameraDirection.BACK, cameraMonitorViewId);
        // We connect to the camera
        phoneCam.openCameraDevice();
        // we use define a variable for the detector so we can
        // call getLocation later
        SimpleSkystoneOpenCVDetector detector = new SimpleSkystoneOpenCVDetector();
        // we set the pipeline to our detector
        phoneCam.setPipeline(detector);
        // and start streaming. Remember to change the camera rotation
        // you can choose from these options (press g d then Ctrl-O)
        phoneCam.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT);
        // the processFrame function will be called for every new frame in the stream
        waitForStart();
        while (opModeIsActive()) {
            // Now we can do something with the robot once we know the location
            if (detector.getLocation() == SkystoneDetectorBase.Location.NONE) {
                // ...
            } else if (detector.getLocation() == SkystoneDetectorBase.Location.LEFT) {
                // ...
            } else {
                // ...
            }
        }

        // that's it for this tutorial, i hope you learn something
        // if you have any questions or concerns, put it in the comments. thanks for watching
    }
}
