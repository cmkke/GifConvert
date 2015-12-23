package getting.media;

import java.io.File;

public class MediaConvertParameters {

    private final File videoFile;
    private final int gifFrameRate;
    private final double gifScale;
    private final int gifTime;

    public MediaConvertParameters(File videoFile, int gifFrameRate, double gifScale, int gifTime) {
        this.videoFile = videoFile;
        this.gifFrameRate = gifFrameRate;
        this.gifScale = gifScale;
        this.gifTime = gifTime;
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

    public int getGifTime() {
        return gifTime;
    }
}
