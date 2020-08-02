package org.wolfcorp.skystone.vision;

import org.openftc.easyopencv.OpenCvPipeline;

public abstract class SkystoneDetectorBase extends OpenCvPipeline {
    public enum Location {
        LEFT,
        RIGHT,
        NONE
    }

    protected Location location = Location.NONE;
    public abstract Location getLocation();
}
