package media;

import command.executor.CommandParameters;

import java.io.File;

public abstract class MediaCommandParameters implements CommandParameters {

    private final File inputFile;

    private final long duration;

    public MediaCommandParameters(File inputFile, int duration) {
        this.inputFile = inputFile;
        this.duration = duration;
    }

    public abstract File getOutputFile();

    public File getInputFile() {
        return inputFile;
    }

    public long getDuration() {
        return duration;
    }

}
