package media;

import command.executor.CommandParameters;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MediaConvertParameters implements CommandParameters {

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
     */
    @Override
    public List<String> buildConvertCommand() {
        List<String> command = new ArrayList<>();
        command.add("-y");
        command.add("-ss");
        command.add(gifStartTime);
        command.add("-i");
        command.add(videoFile.getAbsolutePath());
        command.add("-t");
        command.add("" + gifTime);
        command.add("-r");
        command.add("" + gifFrameRate);
        command.add("-vf");
        command.add("scale=iw*" + gifScale + ":ih*" + gifScale);
        command.add(getOutputGifInfo().getAbsolutePath());
        return command;
    }

    public File getOutputGifInfo() {
        return new File(videoFile.getParent(), videoFile.getName() + ".gif");
    }

}
