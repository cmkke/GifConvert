package media;

import command.executor.ExecuteResult;

import java.io.File;
import java.text.NumberFormat;

public class MediaConvertResult extends ExecuteResult {

    private final File outputFile;

    public MediaConvertResult(File outFile, boolean success, boolean canceled, long costTime) {
        super(success, canceled, costTime, null);
        this.outputFile = outFile;
    }

    public File getOutputFile() {
        return outputFile;
    }

    private String getFileSize() {
        return NumberFormat.getNumberInstance().format(outputFile.length() / 1024) + " KB";
    }

    public String getResult() {
        if (isSuccess()) {
            return "转换时间：" + getCostTimeString() + "，转换后大小：" + getFileSize();
        } else {
            return "转换失败！！是否选择了有效的视频文件？";
        }
    }

    private String getCostTimeString() {
        return NumberFormat.getNumberInstance().format(costTime / 1000.0) + " 秒";
    }

}
