package media;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by youfang on 2016/2/5.
 */
public class MP4ConvertParameters extends MediaCommandParameters {

    public MP4ConvertParameters(File inputFile, int duration) {
        super(inputFile, -1);
    }

    @Override
    public File getOutputFile() {
        final String name = getInputFile().getName().substring(0, getInputFile().getName().lastIndexOf("."));
        return new File(getInputFile().getParent(), name + ".mp4");
    }

    @Override
    public List<String> buildConvertCommand() {
        List<String> command = new ArrayList<>();
        command.add("-y");
        command.add("-i");
        command.add(getInputFile().getAbsolutePath());
        command.add(getOutputFile().getAbsolutePath());
        return command;
    }
}
