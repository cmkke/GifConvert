package media;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MediaInfo {

    private static final Pattern DURATION_PATTERN = Pattern.compile("(?<hour>\\d{2}):(?<minute>\\d{2}):(?<second>\\d{2})\\.(\\d+)", Pattern.CASE_INSENSITIVE);

    private static final Pattern VIDEO_SIZE_PATTERN = Pattern.compile("(?<width>\\d{2,4})x(?<height>\\d{2,4})", Pattern.CASE_INSENSITIVE);

    private static final Pattern VIDEO_FRAME_RATE_PATTERN = Pattern.compile("(?<frame>\\d+) fps", Pattern.CASE_INSENSITIVE);

    private static final Pattern VIDEO_DURATION_PATTERN = Pattern.compile("Duration: (?<duration>\\S+)", Pattern.CASE_INSENSITIVE);

    private final long duration;

    private int videoWidth;

    private int videoHeight;

    private int frameRate;

    private String durationDescription;

    public MediaInfo(List<String> messages) {
        parseMediaInfo(messages);

        Matcher matcher = DURATION_PATTERN.matcher(durationDescription);
        if (matcher.matches()) {
            duration = Integer.parseInt(matcher.group("hour")) * 60 * 60 + Integer.parseInt(matcher.group("minute")) * 60 + Integer.parseInt(matcher.group("second"));
        } else {
            duration = -1;
        }
    }

    /**
     * second
     *
     * @return
     */
    public long getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return "" + videoWidth + "x" + videoHeight + ", " + frameRate + "fps, " + durationDescription;
    }

    private void parseMediaInfo(List<String> messages) {
        for (String message : messages) {
            if (message.startsWith("Output ")) {
                break;
            }

            for (String token : message.split(",")) {
                Matcher videoSizeMatcher = VIDEO_SIZE_PATTERN.matcher(token);
                if (videoSizeMatcher.find()) {
                    videoWidth = Integer.parseInt(videoSizeMatcher.group("width"));
                    videoHeight = Integer.parseInt(videoSizeMatcher.group("height"));
                }

                Matcher frameRateMatcher = VIDEO_FRAME_RATE_PATTERN.matcher(token);
                if (frameRateMatcher.find()) {
                    frameRate = Integer.parseInt(frameRateMatcher.group("frame"));
                }

                Matcher videoDurationMatcher = VIDEO_DURATION_PATTERN.matcher(token);
                if (videoDurationMatcher.find()) {
                    durationDescription = videoDurationMatcher.group("duration");
                }
            }
        }
    }

}
