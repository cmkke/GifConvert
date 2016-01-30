package media;

import command.executor.CommandParameters;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MediaInfoParameters implements CommandParameters {

    private final File mediaFile;

    public MediaInfoParameters(File mediaFile) {
        this.mediaFile = mediaFile;
    }

    @Override
    public List<String> buildConvertCommand() {
        List<String> command = new ArrayList<>();
        command.add("-i");
        command.add(mediaFile.getAbsolutePath());
        return command;
    }

}
