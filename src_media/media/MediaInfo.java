package media;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MediaInfo {

    private static final Pattern DURATION_PATTERN = Pattern.compile("(?<hour>\\d{2}):(?<minute>\\d{2}):(?<second>\\d{2})\\.(\\d+)", Pattern.CASE_INSENSITIVE);

    private static final Pattern VIDEO_SIZE_PATTERN = Pattern.compile("(?<width>\\d{2,4})x(?<height>\\d{2,4})", Pattern.CASE_INSENSITIVE);

    private static final Pattern VIDEO_FRAME_RATE_PATTERN = Pattern.compile("(?<frame>[0-9.]+) fps", Pattern.CASE_INSENSITIVE);

    private static final Pattern VIDEO_DURATION_PATTERN = Pattern.compile("Duration: (?<duration>\\S+)", Pattern.CASE_INSENSITIVE);

    private final Point videoSize;

    private final double frameRate;

    private final String durationDescription;

    /**
     * @param messages Output of "ffmpeg -i file"
     */
    public MediaInfo(List<String> messages) {
        videoSize = parseVideoSize(messages);
        frameRate = parseFrameRate(messages);
        durationDescription = parseDuration(messages);
    }

    private Point parseVideoSize(List<String> messages) {
        for (String message : messages) {
            for (String token : message.split(",")) {
                Matcher videoSizeMatcher = VIDEO_SIZE_PATTERN.matcher(token);
                if (videoSizeMatcher.find()) {
                    return new Point(Integer.parseInt(videoSizeMatcher.group("width")), Integer.parseInt(videoSizeMatcher.group("height")));
                }
            }
        }

        return null;
    }

    private double parseFrameRate(List<String> messages) {
        for (String message : messages) {
            for (String token : message.split(",")) {
                Matcher frameRateMatcher = VIDEO_FRAME_RATE_PATTERN.matcher(token);
                if (frameRateMatcher.find()) {
                    return Double.parseDouble(frameRateMatcher.group("frame"));
                }
            }
        }

        return 0;
    }

    private String parseDuration(List<String> messages) {
        for (String message : messages) {
            for (String token : message.split(",")) {
                Matcher videoDurationMatcher = VIDEO_DURATION_PATTERN.matcher(token);
                if (videoDurationMatcher.find()) {
                    return videoDurationMatcher.group("duration");
                }
            }
        }

        return null;
    }

    public static MediaInfo INVALID = new MediaInfo(new ArrayList<>());

    /**
     * second
     *
     * @return
     */
    public double getDuration() {
        Matcher matcher = DURATION_PATTERN.matcher(durationDescription);
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group("hour")) * 60 * 60 + Integer.parseInt(matcher.group("minute")) * 60 + Integer.parseInt(matcher.group("second"));
        }

        return 0;
    }

    @Override
    public String toString() {
        if (videoSize == null || frameRate <= 0 || durationDescription == null) {
            return "";
        }

        return "" + videoSize.x + "x" + videoSize.y + ", " + frameRate + "fps, " + durationDescription;
    }

}
