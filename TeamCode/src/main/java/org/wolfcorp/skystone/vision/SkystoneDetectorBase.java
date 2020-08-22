package org.wolfcorp.skystone.vision;

import org.openftc.easyopencv.OpenCvPipeline;

public abstract class SkystoneDetectorBase extends OpenCvPipeline {
    public enum Location {
        LEFT,
        RIGHT,
        NOT_FOUND
    }

    protected Location location = Location.NOT_FOUND;
    public abstract Location getLocation();
}
