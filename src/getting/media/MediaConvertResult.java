package getting.media;

import java.io.File;
import java.text.NumberFormat;

public class MediaConvertResult {

    private final File outFile;
    private final long costTime;

    public File getOutFile() {
        return outFile;
    }

    public String getCostTime() {
        return NumberFormat.getNumberInstance().format(costTime / 1000.0) + " 秒";
    }

    public MediaConvertResult(long costTime, File outFile) {
        this.costTime = costTime;
        this.outFile = outFile;
    }

    public String getFileSize() {
        return NumberFormat.getNumberInstance().format(outFile.length() / 1024) + " KB";
    }

}
