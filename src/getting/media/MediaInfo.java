package getting.media;

public class MediaInfo {

    private final int videoWidth;
    private final int videoHeight;

    public MediaInfo(int videoWidth, int videoHeight) {
        this.videoWidth = videoWidth;
        this.videoHeight = videoHeight;
    }

    public int getVideoWidth() {
        return videoWidth;
    }

    public int getVideoHeight() {
        return videoHeight;
    }

}
