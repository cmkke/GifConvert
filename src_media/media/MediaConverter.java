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

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MediaConverter extends CommandExecutor {

    public static final List<Integer> SUPPORT_GIF_TIME = Arrays.asList(5, 10, 15, 20, 30, 60);

    public static final Integer DEFAULT_GIF_TIME = SUPPORT_GIF_TIME.get(1);

    public static final List<Double> SUPPORT_GIF_SCALE = Arrays.asList(0.25, 0.5, 0.75, 1.0);

    public static final Double DEFAULT_GIF_SCALE = SUPPORT_GIF_SCALE.get(3);

    public static final List<Integer> SUPPORT_GIF_FRAME_RATE = Arrays.asList(1, 4, 7, 11, 15);

    public static final Integer DEFAULT_GIF_FRAME_RATE = SUPPORT_GIF_FRAME_RATE.get(2);

    public static final List<String> SUPPORT_VIDEO_FORMAT = Arrays.asList("*.mp4", "*.avi", "*.mkv", "*.mov", "*.flv");

    private static final String CONVERTER_NAME = "ffmpeg.exe";

    private static final Pattern VIDEO_START_TIME_PATTERN = Pattern.compile("(\\d{1,2})(:\\d{1,2})?(:\\d{1,2})?(\\.\\d{1,3})?", Pattern.CASE_INSENSITIVE);

    private static final Pattern CONVERT_PROGRESS_PATTERN = Pattern.compile("frame=.+ fps=.+ q=.+ (size|Lsize)=.+ time=(?<hour>\\d{2}):(?<minute>\\d{2}):(?<second>\\d{2}).+ bitrate=.+", Pattern.CASE_INSENSITIVE);

    private DoubleProperty convertProgress = new SimpleDoubleProperty(Double.NaN);

    public MediaConverter() {
        super(MediaConverter.class, CONVERTER_NAME);
    }

    public static boolean validateMediaStartTime(String time) {
        return "".equals(time) || VIDEO_START_TIME_PATTERN.matcher(time).matches();
    }

    public MediaConvertResult convert(@NotNull MediaConvertParameters convertInfo) {
        if (convertInfo.getOutputGifInfo().exists() && convertInfo.getOutputGifInfo().exists()) {
            convertInfo.getOutputGifInfo().delete();
        }

        updateProgressOnUIiThread(Double.NEGATIVE_INFINITY);
        MediaInfo mediaInfo = new MediaInfo(execute(convertInfo.buildMediaInfoCommand(), FXCollections.observableArrayList()).getMessages());

        ListChangeListener<String> changeListener = new ListChangeListener<String>() {

            @Override
            public void onChanged(Change<? extends String> c) {
                while (c.next()) {
                    if (c.wasAdded()) {
                        for (String message : c.getAddedSubList()) {
                            Matcher matcher = CONVERT_PROGRESS_PATTERN.matcher(message);
                            if (matcher.matches()) {
                                final long duration = Integer.parseInt(matcher.group("hour")) * 60 * 60 + Integer.parseInt(matcher.group("minute")) * 60 + Integer.parseInt(matcher.group("second"));
                                updateProgressOnUIiThread(1.0 * duration / convertInfo.getDuration());
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
                convertInfo.getOutputGifInfo(),
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
