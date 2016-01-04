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

    /**
     * ffmpeg [global_options] {[input_file_options] -i input_file} ... {[output_file_options] output_file} ...
     *
     * @return
     */
    public String buildConvertCommand() {
        return " -y "
                + "-ss " + gifStartTime
                + " -i \"" + videoFile.getAbsolutePath() + "\""
                + " -t " + gifTime
                + " -r " + gifFrameRate
                + " -vf scale=iw*" + gifScale + ":ih*" + gifScale
                + " \"" + getOutputGifInfo().getAbsolutePath() + "\"";
    }

    public String buildGetMediaInfoCommand() {
        return " -i \"" + videoFile.getAbsolutePath() + "\"";
    }

    public File getOutputGifInfo() {
        return new File(videoFile.getParent(), videoFile.getName() + ".gif");
    }

}
