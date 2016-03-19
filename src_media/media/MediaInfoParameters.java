package media;

import command.executor.CommandParameters;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MediaInfoParameters implements CommandParameters {

    public MediaInfoParameters(File media) {
        this.media = media;
    }

    private final File media;

    @Override
    public List<String> buildConvertCommand() {
        List<String> command = new ArrayList<>();
        command.add("-i");
        command.add(media.getAbsolutePath());
        return command;
    }

}
