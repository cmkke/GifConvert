package media;

import java.io.File;
import java.util.regex.Pattern;

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

    public static boolean validateMediaStartTime(String time) {
        return "".equals(time) || VIDEO_START_TIME_PATTERN.matcher(time).matches();
    }

    private static final Pattern VIDEO_START_TIME_PATTERN = Pattern.compile("(\\d{1,2})(:\\d{1,2})?(:\\d{1,2})?(\\.\\d{1,3})?", Pattern.CASE_INSENSITIVE);

    public String buildGetMediaInfoCommand() {
        return " -i \"" + videoFile.getAbsolutePath() + "\"";
    }

    public File getOutputGifInfo() {
        return new File(videoFile.getParent(), videoFile.getName() + ".gif");
    }

}
