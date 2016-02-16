package media;

import com.sun.istack.internal.NotNull;
import command.executor.CommandExecuteResult;
import command.executor.CommandExecutor;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MediaConverter extends CommandExecutor {

    private static final String CONVERTER_NAME = "ffmpeg-20160213-git-588e2e3-win64-static.exe";

    private static final Pattern CONVERT_PROGRESS_PATTERN = Pattern.compile("frame=.+ fps=.+ q=.+ (size|Lsize)=.+ time=(?<hour>\\d{2}):(?<minute>\\d{2}):(?<second>\\d{2}).+ bitrate=.+", Pattern.CASE_INSENSITIVE);

    private DoubleProperty convertProgress = new SimpleDoubleProperty(Double.NaN);

    public MediaConverter() {
        super(MediaConverter.class, CONVERTER_NAME);
    }

    public MediaConvertResult convert(@NotNull MediaCommandParameters convertInfo) {
        if (convertInfo.getOutputFile().exists() && convertInfo.getOutputFile().exists()) {
            convertInfo.getOutputFile().delete();
        }

        updateProgressOnUIiThread(Double.NEGATIVE_INFINITY);
        MediaInfo mediaInfo = new MediaInfo(execute(new MediaInfoParameters(convertInfo.getInputFile()), FXCollections.observableArrayList()).getMessages());

        ListChangeListener<String> changeListener = new ListChangeListener<String>() {

            @Override
            public void onChanged(Change<? extends String> c) {
                while (c.next()) {
                    if (c.wasAdded()) {
                        for (String message : c.getAddedSubList()) {
                            Matcher matcher = CONVERT_PROGRESS_PATTERN.matcher(message);
                            if (matcher.matches()) {
                                final long duration = Integer.parseInt(matcher.group("hour")) * 60 * 60 + Integer.parseInt(matcher.group("minute")) * 60 + Integer.parseInt(matcher.group("second"));
                                if (convertInfo.getDuration() > 0) {
                                    updateProgressOnUIiThread(1.0 * duration / convertInfo.getDuration());
                                } else {
                                    updateProgressOnUIiThread(1.0 * duration / mediaInfo.getDuration());
                                }

                            }
                        }
                    }
                }
            }

        };

        ObservableList<String> processStatus = FXCollections.observableArrayList();
        processStatus.addListener(changeListener);
        CommandExecuteResult convertResult = execute(convertInfo, processStatus);
        processStatus.removeListener(changeListener);
        updateProgressOnUIiThread(Double.NaN);

        return new MediaConvertResult(
                mediaInfo,
                convertInfo.getOutputFile(),
                convertResult.isSuccess(),
                convertResult.getCostTime(),
                convertResult.getMessages());
    }

    public DoubleProperty convertProgressProperty() {
        return convertProgress;
    }

    private void updateProgressOnUIiThread(double progress) {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                convertProgress.set(progress);
            }

        });
    }

}
