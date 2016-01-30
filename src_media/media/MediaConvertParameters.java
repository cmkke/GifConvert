package media;

import command.executor.CommandParameters;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MediaConvertParameters implements CommandParameters {

    private final File mediaFile;

    private final int gifFrameRate;

    private final double gifScale;

    private final int duration;

    private final String gifStartTime;

    public MediaConvertParameters(File mediaFile, int gifFrameRate, double gifScale, String gifStartTime, int duration) {
        this.mediaFile = mediaFile;
        this.gifFrameRate = gifFrameRate;
        this.gifScale = gifScale;

        if ("".equals(gifStartTime)) {
            this.gifStartTime = "0";
        } else {
            this.gifStartTime = gifStartTime;
        }

        this.duration = duration;
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
        command.add(mediaFile.getAbsolutePath());
        command.add("-t");
        command.add("" + duration);
        command.add("-r");
        command.add("" + gifFrameRate);
        command.add("-vf");
        command.add("scale=iw*" + gifScale + ":ih*" + gifScale);
        command.add(getOutputGifInfo().getAbsolutePath());
        return command;
    }

    public int getDuration() {
        return duration;
    }

    public MediaInfoParameters buildMediaInfoCommand() {
        return new MediaInfoParameters(mediaFile);
    }

    public File getOutputGifInfo() {
        return new File(mediaFile.getParent(), mediaFile.getName() + ".gif");
    }

}
