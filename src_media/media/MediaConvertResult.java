package media;

import java.io.File;
import java.text.NumberFormat;

public class MediaConvertResult {

    private final boolean convertSuccess;
    private final File outputFile;
    private final long costTime;
    private final MediaInfo mediaInfo;

    public File getOutputFile() {
        return outputFile;
    }

    public String getCostTime() {
        return NumberFormat.getNumberInstance().format(costTime / 1000.0) + " 秒";
    }

    public MediaConvertResult(MediaInfo mediaInfo, long costTime, File outFile, boolean convertSuccess) {
        this.mediaInfo = mediaInfo;
        this.costTime = costTime;
        this.outputFile = outFile;
        this.convertSuccess = convertSuccess;
    }

    public String getFileSize() {
        return NumberFormat.getNumberInstance().format(outputFile.length() / 1024) + " KB";
    }

    public boolean isConvertSuccess() {
        return convertSuccess;
    }

    public MediaInfo getMediaInfo() {
        return mediaInfo;
    }

}
