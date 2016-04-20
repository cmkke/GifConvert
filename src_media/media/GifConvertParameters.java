package media;

import command.executor.Parameters;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GifConvertParameters implements Parameters {

    public static final List<String> SUPPORT_VIDEO_FORMAT = Arrays.asList("*.mp4", "*.avi", "*.mkv", "*.mov", "*.flv");

    private final boolean reverse;

    private final String logo;

    private final File media;

    private final double outputFrameRate;

    private final double outputScale;

    private final double convertStartTime;

    private final double convertDuration;

    public GifConvertParameters(File media, double outputFrameRate, double outputScale, double convertStartTime, double convertDuration, boolean reverse, String logo) {
        this.media = media;
        this.outputFrameRate = outputFrameRate;
        this.outputScale = outputScale;
        this.convertStartTime = convertStartTime;
        this.convertDuration = convertDuration;
        this.reverse = reverse;
        this.logo = logo;
    }

    public double getConvertDuration() {
        return convertDuration;
    }

    public File getMedia() {
        return media;
    }

    /**
     * ffmpeg [global_options] {[input_file_options] -i input_file} ... {[output_file_options] output_file} ...
     */
    @Override
    public List<String> build() {
        List<String> command = new ArrayList<>();
        command.add("-y");
        command.add("-ss");
        command.add("" + convertStartTime);
        command.add("-i");
        command.add(media.getAbsolutePath());
        command.add("-i");
        command.add(new Logo(logo).create().getAbsolutePath());

        if (!reverse) {
            command.add("-t");
            command.add("" + Math.min(30, convertDuration));
        }

        command.add("-r");
        command.add("" + outputFrameRate);

        command.add("-filter_complex");
        String filter = "scale=iw*" + outputScale + ":ih*" + outputScale;
        if (reverse) {
            filter += ",trim=end=" + Math.min(30, convertDuration) + ",reverse";
        }
        filter += ",overlay=x=W-w-10:y=H-h-10";
        command.add(filter);

        command.add(getOutputFile().getAbsolutePath());
        return command;
    }

    public File getOutputFile() {
        return new File(media.getParent(), media.getName() + ".gif");
    }

}
