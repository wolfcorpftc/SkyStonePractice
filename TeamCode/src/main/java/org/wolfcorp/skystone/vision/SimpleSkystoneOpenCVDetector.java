package org.wolfcorp.skystone.vision;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

// CHUNK 1 ====

// Hello, I am Zhenkai from FTC Team Wolf Corp 12525.

// switch to OpenFTC/EasyOpenCV tab

// In this video, we will set up EasyOpenCV for
// FTC and detect Skystones from last season. EasyOpenCV is a SDK created by OpenFTC that allows
// FTC programmers use cameras with OpenCV easily.

// switch to SDK tab

// First you need to download the Skystone SDK from GitHub if you don't have it already. To set up
// EasyOpenCV, we just have to follow the instructions on GitHub Open Android Studio.

// CHUNK 2 ====

// switch to android studio (make sure the project is closed)

// let's open up the project in android studio

// we need to add `jcenter()` into the repository block in the `build.common.gradle` file
// now go to the `build.gradle` file in the `TeamCode` module and add the dependency

// When you are done, do a Gradle Sync to download the dependencies.
// Then we need to put the OpenCV library into
// the `FIRST` folder on the robot controller's storage.
// Enable MTP mode on your phone and drag and drop the `libOpenCvNative.so` file into the FIRST folder

// to identify SkyStones using OpenCV we have to create a pipeline.
// A pipeline basically accepts video frame from the camera,
// do some processing in the processFrame() Function,
// and return an image to be shown on the robot controller's screen, assuming you are using a phone.
// You can add annotations like rectangles before you return so you can see what the algorithm is doing.

// CHUNK 3 ====

// to get started, we create a class that inherits OpenCvPipeline,
// we need to define the processFrame() function
public class SimpleSkystoneOpenCVDetector extends SkystoneDetectorBase {
    static final double PERCENT_COLOR_THRESHOLD = 0.3;
    @Override
    public Mat processFrame(Mat input) {
        // now the input Mat, ie the input Matrix, is a video frame we get from the camera.
        // We want to keep it untouched, so here we create a working copy variable called mat and
        // initialize it with input.clone(), which performs a deep copy instead of just copying
        // a pointer to the matrix.
        Mat mat = new Mat();

        // Here we convert the matrix to use HSV. RGB isn't good for object detection
        // most of the time; color ranges in RGB are often not very intuitive or useful.
        // There are multiple options for Skystone detection but here I use HSV,
        // you can also choose YUV.
        Imgproc.cvtColor(input, mat, Imgproc.COLOR_RGB2HSV);

        // What we want to do is to detect the stones instead of the SkyStones.
        // They are easier to detect because it's unlikely that you can see color yellow
        // anywhere other than on the stones.

        // For our team, our camera was constrained to the upright position, so it can only see two
        // stones.
        // We start by defining an HSV range
        Scalar lowHSV = new Scalar(26, 100, 100); // lower bound HSV for yellow
        Scalar highHSV = new Scalar(31, 255, 255); // higher bound HSV for yellow
        // This happens to be a good enough HSV range to identify yellow

        // We apply thresholding to our image using the HSV range, which would show us the part
        // of the image that is yellow
        // We create a new variable for the new thresholded matrix
        Mat thresh = new Mat();
        // and do the actual thresholding
        Core.inRange(mat, lowHSV, highHSV, thresh);
        // Now, the regions in the HSV range will turn white, and the rest will be black.

        // CHUNK 4 ====

        // Afterwards, we check to see what percentage of the matrix is white. Since white in HSV
        // always has a value of 255, we can determine the percentage by averaging the value
        // channel, and dividing it by 255

        // Since the robot always starts at the same position, we will know where the stones are on
        // the image. We can try to these specific regions. These ranges depends on the position of
        // your camera, so here I'm just going to just split the image into 4 quarters and assumed
        // the bottom left and bottom right contains the stones. If you want to determine the actual
        // rectangle, you have to determine that experimentally by drawing the rectangle on the
        // screen and adjust as needed.
        // like this Imgproc.rectangle(result, rect, new Scalar(211, 47, 47));
        Mat left = thresh.submat(
                thresh.rows() / 2, thresh.rows(), 0, thresh.cols() / 2);
        Mat right = thresh.submat(
                thresh.rows() / 2, thresh.rows(), thresh.cols() / 2, thresh.cols());

        int area = left.rows() * left.cols();
        double leftValue = Core.sumElems(left).val[2] / area / 255;
        double rightValue = Core.sumElems(right).val[2] / area / 255;

        // CHUNK 5 ====

        // We should define some threshold for the percentages. If the percentage of yellow if lower
        // than some threshold, then it's a skystone. You want to tune the constant later.
        // go define PERCENT_COLOR_THRESHOLD

        // We say that a side has a regular stone if the percentage is higher than our threshold
        boolean stoneLeft = leftValue > PERCENT_COLOR_THRESHOLD;
        boolean stoneRight = rightValue > PERCENT_COLOR_THRESHOLD;

        // Now we have everything we need to determine the location of the stone, if both stones
        // are regular, location = NONE. If the regular stone is on the left, then the SkyStone
        // must be on the right, and same for the right side
        // We should store the result in some variable so we can access it from our opmode we will
        // define later. There are only three possible location for Skystone, left, right or not
        // found. We can define an enumeration for this.
        // (go define enum Location and location var.
        if (stoneLeft && stoneRight)
            location = Location.NONE;
        else if (stoneLeft)
            location = Location.RIGHT;
        else
            location = Location.LEFT;

        // You can return whatever you want to see on your phone's see
        // Here I just return the thresholded matrix.
        return thresh;
    }

    // here we create a getter for the location field
    @Override
    public Location getLocation() {
        return location;
    }
}
