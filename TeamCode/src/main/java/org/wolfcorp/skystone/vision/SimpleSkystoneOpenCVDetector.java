package org.wolfcorp.skystone.vision;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

// CHUNK 1 ====

// Hello, I am Zhenkai from FTC Team Wolf Corp 12525.

// switch to OpenFTC/EasyOpenCV tab

// In this video, we will set up EasyOpenCV for FTC and detect Skystones from last season.
// EasyOpenCV is an SDK created by OpenFTC that allows FTC programmers use cameras with OpenCV
// easily.

// switch to SDK tab

// First you need to download the Skystone SDK from GitHub if you don't have it already. To set up
// EasyOpenCV, we just have to follow the instructions on GitHub.

// CHUNK 2 ====

// switch to android studio (make sure the project is closed)

// let's open up the project in android studio

// we need to add a line in the `build.common.gradle` file into the repositories block
// now go to the `build.gradle` file in the `TeamCode` module and add the dependency

// When you are done, do a Gradle Sync to download the dependencies. Then we need to download the
// OpenCV library and put it in the `FIRST` folder on the robot controller's storage. Enable MTP
// mode on your phone and drag and drop the `libOpenCvNative.so` file into the FIRST folder

// to identify SkyStones using OpenCV we have to create an OpenCV pipeline. A pipeline basically
// accepts video frame from the camera, processes the frame in the processFrame() Function, and
// returns an image which can be displayed on the robot controller's screen. You can also add
// annotations like rectangles within the processFrame() so you can see what the algorithm is doing.

// CHUNK 3 ====

// to get started, we create a class that inherits OpenCvPipeline,
public class SimpleSkystoneOpenCVDetector extends SkystoneDetectorBase {
    Telemetry telemetry;

    // Since my phone can capture two stones at once, we only have two regions of interest, the
    // LEFT_ROI and the RIGHT_ROI. ROI is an abbreviation for region of interest, if that was not
    // clear to you.
    static final Rect LEFT_ROI = new Rect(
            new Point(60, 35),
            new Point(120, 75));
    static final Rect RIGHT_ROI = new Rect(
            new Point(140, 35),
            new Point(200, 75));
    static final double PERCENT_COLOR_THRESHOLD = 0.3;

    Mat mat = new Mat();

    // Let's start with the constructor. It's fairly simple, we just need the Telemetry object which
    // will be passed as argument from the Opmode, so we can view information on the driver station
    public SimpleSkystoneOpenCVDetector(Telemetry t) { telemetry = t; }

    // then we need to override the processFrame() function
    @Override
    public Mat processFrame(Mat input) {
        // now, the input matrix is a video frame we get from the camera. We want to keep the input
        // matrix as it is, so let's create a working copy variable, called mat. We need to declare
        // it as a field so OpenCV only need to allocate the buffer once.

        // Here we convert the matrix from RGB to HSV.
        Imgproc.cvtColor(input, mat, Imgproc.COLOR_RGB2HSV);
        // RGB isn't good for object detection most of the time; It is difficult to specify color
        // ranges in RGB. You can also convert it to YUV but here I use HSV because my camera can
        // only detect two stones at a time at the time of the competition, you will see why later.

        // What we want to do is to detect the stones instead of the SkyStones.
        // They are easier to detect because it's unlikely that you can see color yellow
        // anywhere other than on the stones.

        // For our team, our camera was constrained to the upright position, so it can only see two
        // stones at a time. We start by defining an HSV range to identify yellow
        Scalar lowHSV = new Scalar(23, 50, 70); // lower bound HSV for yellow
        Scalar highHSV = new Scalar(32, 255, 255); // higher bound HSV for yellow
        // the x values of the two scalars represent a range of Hue, y values represent a range of
        // Saturation, and the z values represent a range of Value. Only if the HSV value at hand
        // fits within all three ranges will it be considered yellow.

        // Then we apply thresholding to our image using the HSV range we just created,
        // which would show us the part of the image that is yellow.
        Core.inRange(mat, lowHSV, highHSV, mat);
        // PRESS K on inRange
        // So the mat is the src matrix, lowHSV and high HSV are the boundaries, and we reuse mat
        // for the destination matrix, so that we don't have to create another one

        // After thresholding, the matrix becomes a grayscale image; the regions that have colors
        // within the HSV range will turn white, and the rest will become black.

        // CHUNK 4 ====

        // Since the robot always starts at the same position, we will know where the stones are on
        // the image. We focus on these regions instead of the whole image. Where these regions are
        // depends on the position of your camera, and you can determine them by drawing rectangles
        // on the image and adjust until the rectangles fits within where the stones are on the
        // image, which I will demonstrate later.

        // I had already found the regions that my phone is going to use. We can define them as
        // Rectangles so we can reuse these coordinates without having to create multiple constants.

        // GO TO THE TOP (LEFT_ROI, RIGHT_ROI)

        // now that we've defined the regions of interest,
        // we can extract them from the image like so.
        Mat left = mat.submat(LEFT_ROI);
        Mat right = mat.submat(RIGHT_ROI);

        // Now we check to see what percentage of the matrix became white to identify the
        // stones. We can determine the percentage by averaging the brightness, and dividing it by
        // 255, which is the max value for a grayscale pixel
        // Note that LEFT_ROI and RIGHT_ROI have the same area, but it's always a good idea to
        // use the corresponding rectangles for each side

        double leftValue = Core.sumElems(left).val[0] / LEFT_ROI.area() / 255;
        double rightValue = Core.sumElems(right).val[0] / RIGHT_ROI.area() / 255;

        // after you have used the submatrices, make sure to release them
        left.release();
        right.release();

        // to help with debugging, you can use telemetry to display the values used in the
        // calculation
        telemetry.addData("Left raw value", (int) Core.sumElems(left).val[0]);
        telemetry.addData("Right raw value", (int) Core.sumElems(right).val[0]);
        telemetry.addData("Left value", Math.round(leftValue * 100) + "%");
        telemetry.addData("Right value", Math.round(rightValue * 100) + "%");

        // CHUNK 5 ====

        // now we can begin identifying the stones If the percentage of yellow is higher than some
        // threshold, then it's a regular stone. You may want to tune the constant later.

        // go define PERCENT_COLOR_THRESHOLD

        // We say that a side has a regular stone if the percentage is higher than our threshold
        boolean stoneLeft = leftValue > PERCENT_COLOR_THRESHOLD;
        boolean stoneRight = rightValue > PERCENT_COLOR_THRESHOLD;

        // Now we have everything we need to determine the location of the skystone,
        // TYPE WHILE YOU SAY: if both stones are regular, we set the location to not found. If the
        // regular stone is on the left, then the SkyStone must be on the right, otherwise the
        // it's on the left

        // We should store the result in some variable so we can access it from our opmode
        // which we will define later. There are only three possible location for Skystone, left,
        // right or not found. We can define an enum for this.
        // (go define enum Location and location var)
        
        // we then create a getter for the private location field

        if (stoneLeft && stoneRight) {
            location = Location.NOT_FOUND;
            telemetry.addData("Skystone Location", "none");
        }
        else if (stoneLeft) {
            location = Location.RIGHT;
            telemetry.addData("Skystone Location", "right");
        }
        else {
            location = Location.LEFT;
            telemetry.addData("Skystone Location", "left");
        }
        telemetry.update();

        // We should draw rectangles to visualize the location of the stones and the skytones
        // we first convert the grayscale image back to RGB
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_GRAY2RGB);
        // then we define two colors so that we can differentiate a stone from a skystone
        // for simplicity, we will use red for a stone and green for a skystone, which is super
        // easy to define in RGB
        Scalar colorStone = new Scalar(255, 0, 0);
        Scalar colorSkystone = new Scalar(0, 255, 0);

        // the rest is simple, we draw the rectangles we defined earlier onto the matrix
        Imgproc.rectangle(mat, LEFT_ROI, location == Location.LEFT? colorSkystone:colorStone);
        Imgproc.rectangle(mat, RIGHT_ROI, location == Location.RIGHT? colorSkystone:colorStone);
        // we used the ternary operator, aka a conditional expression, which is basically a
        // mini if statement that chooses which color to use based on the location of the skystone

        // now that we are finished with skystone identification, we can return the matrix
        // which EasyOpenCV will draw on the screen for us
        return mat;
    }

    @Override
    public Location getLocation() {
        return location;
    }
}
