package media;

import java.io.File;
import java.text.NumberFormat;

public class MediaConvertResult {

    private final boolean convertSuccess;
    private final File outFile;
    private final long costTime;

    public File getOutFile() {
        return outFile;
    }

    public String getCostTime() {
        return NumberFormat.getNumberInstance().format(costTime / 1000.0) + " 秒";
    }

    public MediaConvertResult(long costTime, File outFile, boolean convertSuccess) {
        this.costTime = costTime;
        this.outFile = outFile;
        this.convertSuccess = convertSuccess;
    }

    public String getFileSize() {
        return NumberFormat.getNumberInstance().format(outFile.length() / 1024) + " KB";
    }

    public boolean isConvertSuccess() {
        return convertSuccess;
    }

}
