package media;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MP4ConvertParameters extends MediaCommandParameters {

    public MP4ConvertParameters(File inputFile, int duration) {
        super(inputFile, -1);
    }

    @Override
    public File getOutputFile() {
        return new File(getInputFile().getParent(), getInputFile().getName() + ".mp4");
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
