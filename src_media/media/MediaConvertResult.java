package media;

import command.executor.CommandExecuteResult;

import java.io.File;
import java.text.NumberFormat;

public class MediaConvertResult extends CommandExecuteResult {

    private final File outputFile;

    private final MediaInfo mediaInfo;

    public MediaConvertResult(MediaInfo mediaInfo, File outFile, CommandExecuteResult commandExecuteResult) {
        super(commandExecuteResult);
        this.mediaInfo = mediaInfo;
        this.outputFile = outFile;
    }

    public File getOutputFile() {
        return outputFile;
    }

    public String getFileSize() {
        return NumberFormat.getNumberInstance().format(outputFile.length() / 1024) + " KB";
    }

    public MediaInfo getMediaInfo() {
        return mediaInfo;
    }

}
