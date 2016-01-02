package media;

import java.io.File;

public class MediaConvertParameters {

    private final File videoFile;
    private final int gifFrameRate;
    private final double gifScale;
    private final int gifTime;
    private final String gifStartTime;

    public MediaConvertParameters(File videoFile, int gifFrameRate, double gifScale, String gifStartTime, int gifTime) {
        this.videoFile = videoFile;
        this.gifFrameRate = gifFrameRate;
        this.gifScale = gifScale;

        if ("".equals(gifStartTime)) {
            this.gifStartTime = "0";
        } else {
            this.gifStartTime = gifStartTime;
        }

        this.gifTime = gifTime;
    }

    public String buildConvertCommand() {
        return " -y -i \"" + videoFile.getAbsolutePath() + "\""
                + " -t " + gifTime
                + " -ss " + gifStartTime
                + " -r " + gifFrameRate
                + " -vf scale=iw*" + gifScale + ":ih*" + gifScale
                + " \"" + buildGifFile().getAbsolutePath() + "\"";
    }

    public String buildGetMediaInfoCommand() {
        return " -i \"" + videoFile.getAbsolutePath() + "\"";
    }

    public File buildGifFile() {
        return new File(videoFile.getParent(), videoFile.getName() + ".gif");
    }

}
