package media;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class GifConvertParameters extends MediaCommandParameters {

    public static final List<Integer> SUPPORT_GIF_TIME = Arrays.asList(5, 10, 15, 20, 30, 60);

    public static final Integer DEFAULT_GIF_TIME = SUPPORT_GIF_TIME.get(1);

    public static final List<Double> SUPPORT_GIF_SCALE = Arrays.asList(0.25, 0.5, 0.75, 1.0);

    public static final Double DEFAULT_GIF_SCALE = SUPPORT_GIF_SCALE.get(3);

    public static final List<Integer> SUPPORT_GIF_FRAME_RATE = Arrays.asList(1, 4, 7, 11, 15);

    public static final Integer DEFAULT_GIF_FRAME_RATE = SUPPORT_GIF_FRAME_RATE.get(2);

    public static final List<String> SUPPORT_VIDEO_FORMAT = Arrays.asList("*.mp4", "*.avi", "*.mkv", "*.mov", "*.flv");

    private static final Pattern VIDEO_START_TIME_PATTERN = Pattern.compile("(\\d{1,2})(:\\d{1,2})?(:\\d{1,2})?(\\.\\d{1,3})?", Pattern.CASE_INSENSITIVE);

    private final int gifFrameRate;

    private final double gifScale;

    private final String gifStartTime;

    public GifConvertParameters(File mediaFile, int gifFrameRate, double gifScale, String gifStartTime, int duration) {
        super(mediaFile, duration);

        this.gifFrameRate = gifFrameRate;
        this.gifScale = gifScale;

        if ("".equals(gifStartTime)) {
            this.gifStartTime = "0";
        } else {
            this.gifStartTime = gifStartTime;
        }
    }

    public static boolean validateMediaStartTime(String time) {
        return "".equals(time) || VIDEO_START_TIME_PATTERN.matcher(time).matches();
    }

    /**
     * ffmpeg [global_options] {[input_file_options] -i input_file} ... {[output_file_options] output_file} ...
     */
    @Override
    public List<String> buildConvertCommand() {
        List<String> command = new ArrayList<>();
        command.add("-y");
        command.add("-ss");
        command.add(gifStartTime);
        command.add("-i");
        command.add(getInputFile().getAbsolutePath());
        command.add("-t");
        command.add("" + getDuration());
        command.add("-r");
        command.add("" + gifFrameRate);
        command.add("-vf");
        command.add("scale=iw*" + gifScale + ":ih*" + gifScale);
        command.add(getOutputFile().getAbsolutePath());
        return command;
    }

    @Override
    public File getOutputFile() {
        return new File(getInputFile().getParent(), getInputFile().getName() + ".gif");
    }
}
