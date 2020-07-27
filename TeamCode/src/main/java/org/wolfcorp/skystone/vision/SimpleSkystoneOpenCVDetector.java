package org.wolfcorp.skystone.vision;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

public class SimpleSkystoneOpenCVDetector extends OpenCvPipeline {
    @Override
    public Mat processFrame(Mat input) {
        Mat mat = new Mat();
        Imgproc.cvtColor(input, mat, Imgproc.COLOR_RGB2HSV);

        // this happens to be a good HSV range to identify yellow
        Scalar lowHSV = new Scalar(20, 100, 50); // lower bound HSV for yellow
        Scalar highHSV = new Scalar(30, 255, 255); // higher bound HSV for yellow

        Mat thresh = new Mat();

        // We'll get a black and white image. The white regions represent the regular stones.
        // inRange(): thresh[i][j] = {255,255,255} if mat[i][i] is within the range
        Core.inRange(mat, lowHSV, highHSV, thresh);

        Mat left = thresh.submat(thresh.rows() / 2, thresh.rows(), 0, thresh.cols() / 2);
        Mat center = thresh.submat()
        Mat right = thresh.submat(thresh.rows() / 2, thresh.rows(), thresh.cols() / 2, thresh.cols());
        // HSV has 3 values. It doesn't really matter what the index is
        // here because the thresholded value should the same throughout
        double leftValue = Core.sumElems(left).val[0];
        double rightValue = Core.sumElems(right).val[0];

        return input;
    }
}
