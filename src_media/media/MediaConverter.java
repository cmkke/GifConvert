package media;

import com.sun.istack.internal.NotNull;
import executor.CommandExecutor;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MediaConverter {

    private static final String CONVERTER_NAME = "ffmpeg.exe";
    private static final CommandExecutor MEDIA_CONVERTER_COMMAND_EXECUTOR = new CommandExecutor(MediaConverter.class, CONVERTER_NAME);

    private MediaConverter() {
    }

    public static final List<Integer> SUPPORT_GIF_TIME = Arrays.asList(5, 10, 15, 20);
    public static final Integer DEFAULT_GIF_TIME = 10;

    public static final List<Double> SUPPORT_GIF_SCALE = Arrays.asList(0.25, 0.5, 0.75, 1.0);
    public static final Double DEFAULT_GIF_SCALE = 1.0;

    public static final List<Integer> SUPPORT_GIF_FRAME_RATE = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,
            13, 14, 15);
    public static final Integer DEFAULT_GIF_FRAME_RATE = 8;

    public static final List<String> SUPPORT_VIDEO_FORMAT = Arrays.asList("*.mp4", "*.avi", "*.mkv", "*.mov");

    public static MediaInfo getMediaInfo(@NotNull MediaConvertParameters convertInfo) {
        List<String> messages = MEDIA_CONVERTER_COMMAND_EXECUTOR.execute(convertInfo.buildGetMediaInfoCommand()).getMessage();
        for (String message : messages) {
            for (String token : message.split(",")) {
                Matcher matcher = VIDEO_SIZE_PATTERN.matcher(token);
                if (matcher.find()) {
                    final int width = Integer.parseInt(matcher.group(1));
                    final int height = Integer.parseInt(matcher.group(2));
                    return new MediaInfo(width, height);
                }
            }
        }

        return null;
    }

    private static final Pattern VIDEO_SIZE_PATTERN = Pattern.compile("(\\d{2,4})x(\\d{2,4})", Pattern
            .CASE_INSENSITIVE);

    public static MediaConvertResult convert(@NotNull MediaConvertParameters convertInfo) {
        final long startTime = System.currentTimeMillis();
        final boolean convertSuccess = MEDIA_CONVERTER_COMMAND_EXECUTOR.execute(convertInfo.buildConvertCommand()).isSuccess();
        return new MediaConvertResult(System.currentTimeMillis() - startTime, convertInfo.buildGifFile(), convertSuccess);
    }

}
