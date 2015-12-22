package getting.media;

import com.sun.istack.internal.NotNull;
import javafx.util.Callback;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MediaConverter {

    private static final String CONVERTER_NAME = "ffmpeg.exe";
    private static final File CONVERTER = new File(System.getProperty("java.io.tmpdir"), CONVERTER_NAME);

    private MediaConverter() {
    }

    private static void copyConverterToTempDirectory() {
        try {
            OutputStream outputStream = new FileOutputStream(CONVERTER);
            InputStream inputStream = MediaConverter.class.getResourceAsStream(CONVERTER_NAME);
            byte[] buffer = new byte[4096];
            while (true) {
                int readCount = inputStream.read(buffer);
                if (readCount == -1) {
                    break;
                }

                outputStream.write(buffer, 0, readCount);
            }
            inputStream.close();
            outputStream.close();
            System.out.println("converter has copied to temp directory");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void ensureConverterAvaliable() {
        if (CONVERTER.exists()) {
            System.out.println("media converter exist");
            return;
        }

        copyConverterToTempDirectory();
    }

    public static final List<Double> SUPPORT_GIF_SCALE = Arrays.asList(0.25, 0.5, 0.75, 1.0);
    public static final Double DEFAULT_GIF_SCALE = 1.0;
    public static final List<Integer> SUPPORT_GIF_FRAME_RATE = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,
            13, 14, 15);
    public static final Integer DEFAULT_GIF_FRAME_RATE = 8;
    public static final List<String> SUPPORT_VIDEO_FORMAT = Arrays.asList("*.mp4", "*.avi");

    private static MediaInfo getVideoInfo(InputStream stream) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            boolean readingVideoInfo = false;
            while (true) {
                String message = reader.readLine();
                if (message == null) {
                    break;
                }

                if (message.startsWith("Input")) {
                    readingVideoInfo = true;
                }

                if (message.startsWith("Output")) {
                    readingVideoInfo = false;
                }

                if (readingVideoInfo) {
                    for (String token : message.split(",")) {
                        Matcher matcher = VIDEO_SIZE_PATTERN.matcher(token);
                        if (matcher.find()) {
                            final int width = Integer.parseInt(matcher.group(1));
                            final int height = Integer.parseInt(matcher.group(2));
                            return new MediaInfo(width, height);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static final Pattern VIDEO_SIZE_PATTERN = Pattern.compile("(\\d{2,4})x(\\d{2,4})", Pattern
            .CASE_INSENSITIVE);

    public static void convert(@NotNull MediaConvertParameters convertInfo, @NotNull Callback<MediaConvertResult, Void>
            notify) {
        ensureConverterAvaliable();

        final long startTime = System.currentTimeMillis();
        final File gifFile = new File(convertInfo.getVideoFile().getParent(), convertInfo.getVideoFile().getName() +
                ".gif");
        final String convertCommand = CONVERTER.getAbsolutePath() + " -y -i \"" + convertInfo.getVideoFile()
                .getAbsolutePath() + "\" -t 10 -r " + convertInfo.getGifFrameRate() + " -vf scale=iw*" + convertInfo
                .getGifScale() + ":ih*" + convertInfo.getGifScale() + " \"" + gifFile.getAbsolutePath() + "\"";
        System.out.println(convertCommand);
        try {
            Process converterProcess = Runtime.getRuntime().exec(convertCommand);
            getVideoInfo(converterProcess.getErrorStream());
            converterProcess.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        notify.call(new MediaConvertResult(System.currentTimeMillis() - startTime, gifFile));
    }


}
