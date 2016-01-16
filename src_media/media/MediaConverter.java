package media;

import com.sun.istack.internal.NotNull;
import executor.CommandExecuteResult;
import executor.CommandExecutor;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MediaConverter {

    public static final List<Integer> SUPPORT_GIF_TIME = Arrays.asList(5, 10, 15, 20, 30, 60);
    public static final Integer DEFAULT_GIF_TIME = SUPPORT_GIF_TIME.get(1);
    public static final List<Double> SUPPORT_GIF_SCALE = Arrays.asList(0.25, 0.5, 0.75, 1.0);
    public static final Double DEFAULT_GIF_SCALE = SUPPORT_GIF_SCALE.get(3);
    public static final List<Integer> SUPPORT_GIF_FRAME_RATE = Arrays.asList(1, 4, 7, 11, 15);
    public static final Integer DEFAULT_GIF_FRAME_RATE = SUPPORT_GIF_FRAME_RATE.get(2);
    public static final List<String> SUPPORT_VIDEO_FORMAT = Arrays.asList("*.mp4", "*.avi", "*.mkv", "*.mov");
    private static final String CONVERTER_NAME = "ffmpeg.exe";
    private static final CommandExecutor MEDIA_CONVERTER_COMMAND_EXECUTOR = new CommandExecutor(MediaConverter.class, CONVERTER_NAME);
    private static final Pattern VIDEO_SIZE_PATTERN = Pattern.compile("(\\d{2,4})x(\\d{2,4})", Pattern.CASE_INSENSITIVE);
    private static final Pattern VIDEO_FRAME_RATE_PATTERN = Pattern.compile("(\\d+) fps", Pattern.CASE_INSENSITIVE);
    private static final Pattern VIDEO_DURATION_PATTERN = Pattern.compile("Duration: (\\S+)", Pattern.CASE_INSENSITIVE);

    private MediaConverter() {
    }

    public static MediaInfo getMediaInfo(@NotNull MediaConvertParameters convertInfo) {
        return parseMediaInfo(MEDIA_CONVERTER_COMMAND_EXECUTOR.execute(convertInfo.buildGetMediaInfoCommand()).getMessage());
    }

    private static MediaInfo parseMediaInfo(List<String> messages) {
        int width = -1;
        int height = -1;
        int frameRate = -1;
        String duration = null;

        for (String message : messages) {
            if (message.startsWith("Output ")) {
                break;
            }

            for (String token : message.split(",")) {
                Matcher videoSizeMatcher = VIDEO_SIZE_PATTERN.matcher(token);
                if (videoSizeMatcher.find()) {
                    width = Integer.parseInt(videoSizeMatcher.group(1));
                    height = Integer.parseInt(videoSizeMatcher.group(2));
                }

                Matcher frameRateMatcher = VIDEO_FRAME_RATE_PATTERN.matcher(token);
                if (frameRateMatcher.find()) {
                    frameRate = Integer.parseInt(frameRateMatcher.group(1));
                }

                Matcher videoDurationMatcher = VIDEO_DURATION_PATTERN.matcher(token);
                if (videoDurationMatcher.find()) {
                    duration = videoDurationMatcher.group(1);
                }
            }
        }

        if (width == -1 || height == -1 || frameRate == -1 || duration == null) {
            return null;
        }

        return new MediaInfo(width, height, frameRate, duration);
    }

    public static MediaConvertResult convert(@NotNull MediaConvertParameters convertInfo) {
        final long startTime = System.currentTimeMillis();
        convertInfo.getOutputGifInfo().delete();
        CommandExecuteResult convertResult = MEDIA_CONVERTER_COMMAND_EXECUTOR.execute(convertInfo.buildConvertCommand());
        return new MediaConvertResult(parseMediaInfo(convertResult.getMessage()),
                System.currentTimeMillis() - startTime,
                convertInfo.getOutputGifInfo(),
                convertResult.isSuccess());
    }

}
