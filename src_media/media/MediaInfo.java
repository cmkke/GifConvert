package media;

public class MediaInfo {

    private final int videoWidth;

    private final int videoHeight;

    private final int frameRate;

    private final String duration;

    public MediaInfo(int videoWidth, int videoHeight, int frameRate, String duration) {
        this.videoWidth = videoWidth;
        this.videoHeight = videoHeight;
        this.frameRate = frameRate;
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "" + videoWidth + "x" + videoHeight + ", " + frameRate + "fps, " + duration;
    }

}
