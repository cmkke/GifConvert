package media;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GifConvertParameters extends MediaCommandParameters {

    public static final List<String> SUPPORT_VIDEO_FORMAT = Arrays.asList("*.mp4", "*.avi", "*.mkv", "*.mov", "*.flv");

    private final double gifFrameRate;

    private final double gifScale;

    private final double gifStartTime;

    public GifConvertParameters(File mediaFile, double gifFrameRate, double gifScale, double gifStartTime, double duration) {
        super(mediaFile, duration);

        this.gifFrameRate = gifFrameRate;
        this.gifScale = gifScale;
        this.gifStartTime = gifStartTime;
    }

    /**
     * ffmpeg [global_options] {[input_file_options] -i input_file} ... {[output_file_options] output_file} ...
     */
    @Override
    public List<String> buildConvertCommand() {
        List<String> command = new ArrayList<>();
        command.add("-y");
        command.add("-ss");
        command.add("" + gifStartTime);
        command.add("-i");
        command.add(getInputFile().getAbsolutePath());
        command.add("-t");
        command.add("" + Math.min(30, getDuration()));
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
