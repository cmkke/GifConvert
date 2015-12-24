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

    public String buildConvertCommand() {
        return " -y -i \"" + videoFile.getAbsolutePath() + "\" -t " + gifTime + " -r " + gifFrameRate + " -vf " +
                "scale=iw*" + gifScale + ":ih*" + gifScale + " \"" + buildGifFile().getAbsolutePath() + "\"";
    }

    public File buildGifFile() {
        return new File(videoFile.getParent(), videoFile.getName() + ".gif");
    }
}
