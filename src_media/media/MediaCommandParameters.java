package media;

import command.executor.CommandParameters;

import java.io.File;

public abstract class MediaCommandParameters implements CommandParameters {

    private final File inputFile;

    private final double outputFrameRate;

    private final double outputScale;

    private final double convertStartTime;

    private final double convertDuration;

    /**
     * @param inputFile
     * @param convertDuration  In seconds
     * @param outputFrameRate
     * @param outputScale
     * @param convertStartTime In seconds
     */
    public MediaCommandParameters(File inputFile, double convertDuration, double outputFrameRate, double outputScale, double convertStartTime) {
        this.inputFile = inputFile;
        this.convertDuration = convertDuration;
        this.outputFrameRate = outputFrameRate;
        this.outputScale = outputScale;
        this.convertStartTime = convertStartTime;
    }

    public abstract File getOutputFile();

    public File getInputFile() {
        return inputFile;
    }

    public double getConvertDuration() {
        return convertDuration;
    }

    public double getOutputFrameRate() {
        return outputFrameRate;
    }

    public double getOutputScale() {
        return outputScale;
    }

    public double getConvertStartTime() {
        return convertStartTime;
    }

}
