package getting.media;

import java.io.File;

public class MediaConvertParameters {

    private final File videoFile;
    private final int gifFrameRate;
    private final double gifScale;

    public MediaConvertParameters(File videoFile, int gifFrameRate, double gifScale) {
        this.videoFile = videoFile;
        this.gifFrameRate = gifFrameRate;
        this.gifScale = gifScale;
    }

    public double getGifScale() {
        return gifScale;
    }

    public File getVideoFile() {
        return videoFile;
    }

    public int getGifFrameRate() {
        return gifFrameRate;
    }


}
