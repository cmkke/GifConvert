package media;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GifConvertParameters extends MediaCommandParameters {

    public static final List<String> SUPPORT_VIDEO_FORMAT = Arrays.asList("*.mp4", "*.avi", "*.mkv", "*.mov", "*.flv");

    private final boolean reverse;

    public GifConvertParameters(File mediaFile, double gifFrameRate, double gifScale, double convertStartTime, double convertDuration, boolean reverse) {
        super(mediaFile, convertDuration, gifFrameRate, gifScale, convertStartTime);
        this.reverse = reverse;
    }

    /**
     * ffmpeg [global_options] {[input_file_options] -i input_file} ... {[output_file_options] output_file} ...
     */
    @Override
    public List<String> buildConvertCommand() {
        List<String> command = new ArrayList<>();
        command.add("-y");
        command.add("-ss");
        command.add("" + getConvertStartTime());
        command.add("-i");
        command.add(getInputFile().getAbsolutePath());

        if (!reverse) {
            command.add("-t");
            command.add("" + Math.min(30, getConvertDuration()));
        }

        command.add("-r");
        command.add("" + getOutputFrameRate());

        command.add("-vf");
        String filter = "scale=iw*" + getOutputScale() + ":ih*" + getOutputScale();
        if (reverse) {
            filter += ",trim=end=" + Math.min(30, getConvertDuration()) + ",reverse";
        }
        command.add(filter);

        command.add(getOutputFile().getAbsolutePath());
        return command;
    }

    @Override
    public File getOutputFile() {
        return new File(getInputFile().getParent(), getInputFile().getName() + ".gif");
    }

}
