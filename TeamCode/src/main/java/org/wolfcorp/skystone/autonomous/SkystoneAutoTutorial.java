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

// CHUNK 6 ====
// Now that we have a detector pipeline, we can use it to create
// an autonomous opmode
@Autonomous(name="Detect skystone", group="Auto")
public class SkystoneAutoTutorial extends LinearOpMode {
    OpenCvCamera phoneCam;

    @Override
    public void runOpMode() {
        // https://github.com/OpenFTC/EasyOpenCV/blob/master/examples/src/main/java/org/openftc/easyopencv/examples/InternalCameraExample.java
        // We need to do a few things to initialize the camera
        int cameraMonitorViewId = hardwareMap.appContext.getResources()
                .getIdentifier("cameraMonitorViewId",
                        "id", hardwareMap.appContext.getPackageName());
        phoneCam = OpenCvCameraFactory.getInstance()
                .createInternalCamera(OpenCvInternalCamera.CameraDirection.BACK, cameraMonitorViewId);
        phoneCam.openCameraDevice();
        // Now we are connected to the camera
        // we need to declare a variable for the detector so we can
        // call getLocation later
        SimpleSkystoneOpenCVDetector detector = new SimpleSkystoneOpenCVDetector();
        // we set the pipeline to our detector
        phoneCam.setPipeline(detector);
        // and start streaming asynchronously.
        phoneCam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                phoneCam.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT);
                // Remember to change the camera rotation
                // you can choose from these options (press g d then Ctrl-O)
            }
        });
        // the processFrame function will be called for every new frame in the stream

        // CHUNK 7 ====

        waitForStart();
        // Now we can do something with the robot once we know the location
        switch (detector.getLocation()) {
            case LEFT:
                // ...
                break;
            case RIGHT:
                // ...
                break;
            case NONE:
                // ...
                break;
        }
        // when you are done with skystone detection don't forget to stop streaming
        phoneCam.stopStreaming();

        // that's it for this tutorial, i hope you learned something
        // if you have any questions, issues, or concerns, put it in the comments.
        // thanks for watching
    }
}
