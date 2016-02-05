package media;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MediaInfoParameters extends MediaCommandParameters {

    public MediaInfoParameters(File mediaFile) {
        super(mediaFile, -1);
    }

    @Override
    public File getOutputFile() {
        return null;
    }

    @Override
    public List<String> buildConvertCommand() {
        List<String> command = new ArrayList<>();
        command.add("-i");
        command.add(getInputFile().getAbsolutePath());
        return command;
    }

}
