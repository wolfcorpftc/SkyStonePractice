package org.wolfcorp.skystone.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvInternalCamera;
import org.wolfcorp.skystone.vision.SimpleSkystoneOpenCVDetector;

// CHUNK 6 ====
// Now that we have a detector pipeline, we can use it to create
// an autonomous opmode
@Autonomous(name="Skystone Detector", group="Auto")
public class SkystoneAutoTutorial extends SkystoneAuto {
    OpenCvCamera phoneCam;

    @Override
    public void runOpMode() {
        // https://github.com/OpenFTC/EasyOpenCV/blob/master/examples/src/main/java/org/openftc/easyopencv/examples/InternalCameraExample.java
        // We can copy some code from the example to initialize the phoneCam
        int cameraMonitorViewId = hardwareMap.appContext.getResources()
                .getIdentifier("cameraMonitorViewId",
                        "id", hardwareMap.appContext.getPackageName());
        phoneCam = OpenCvCameraFactory.getInstance()
                .createInternalCamera(
                        OpenCvInternalCamera.CameraDirection.BACK, cameraMonitorViewId);
        // Now we are connected to the camera
        // we need to declare a variable for the detector so we can
        // call getLocation later
        SimpleSkystoneOpenCVDetector detector = new SimpleSkystoneOpenCVDetector(telemetry);
        // we set the pipeline to our detector
        phoneCam.setPipeline(detector);
        // and start streaming asynchronously, which is the recommended way to open the camera since
        // EasyOpenCV 1.4
        phoneCam.openCameraDeviceAsync(
                () -> phoneCam.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT)
                // I'm using a lambda expression here, which is basically an anonymous function.
                // since we only have one statement to execute, we can even remove the curly braces
                // around the function and the semicolon too. It looks a lot cleaner.

                // Lambdas are a feature of Java 8, to make use of that we need to change JDK the
                // version of the teamcode module to 1.8. If you prefer using Java 7 for some reason
                // you can use the more verbose syntax in the example
        );
        // Also remember to specify your camera's orientation
        // you can choose from these options (press g d then Ctrl-O)

        // Once the camera starts streaming, the processFrame() function will be called for every
        // new frame in the stream

        // CHUNK 7 ====

        waitForStart();
        while (opModeIsActive());
        if (true) return;
        // Once we know the location, the robot can act accordingly
        switch (detector.getLocation()) {
            case LEFT:
                sidestepLeft(0.5, 5);
            case RIGHT:
                sleep(100);
                Thread threadOpenClaw = new Thread(() -> { robot.openClaw(); });
                Thread threadGoToStone = new Thread(() -> { forward(0.5, 5); });
                threadOpenClaw.start();
                threadGoToStone.start();
                try {
                    threadGoToStone.join();
                    threadOpenClaw.join();
                } catch (InterruptedException e) {
                    telemetry.addData("Exception", "Thread interrupted");
                    telemetry.update();
                }
                break;
            case NOT_FOUND:
                telemetry.addData("Info", "Not doing anything here");
                telemetry.update();
                break;
        }

        // when you are done with skystone detection don't forget to stop streaming
        // so that it consumes less resources
        phoneCam.stopStreaming();

        // That's all for this tutorial; I hope it was helpful to you. if you have any questions,
        // issues, or concerns, put it in the comments. Check the video description for the code
        // used in the tutorial plus some additional links. Thanks for watching.
    }
}
