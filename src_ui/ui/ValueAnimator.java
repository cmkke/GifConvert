package ui;

import javafx.application.Platform;

public abstract class ValueAnimator implements Runnable {

    // 60fps
    private static final long INTERVAL = 15;

    public void start() {
        new Thread() {

            @Override
            public void run() {
                while (currentTime < duration) {
                    updateProgressOnUiThread(1.0 * currentTime / duration);
                    try {
                        Thread.sleep(INTERVAL);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (isCancel) {
                        break;
                    }

                    currentTime += INTERVAL;
                }

                updateProgressOnUiThread(1);
            }

        }.start();
    }

    @Override
    public void run() {
        onAnimate(progress);
    }

    private double progress;

    public ValueAnimator(long duration) {
        this.duration = duration;
    }

    private final long duration;

    private long currentTime;

    public abstract void onAnimate(double progress);

    private void updateProgressOnUiThread(double progress) {
        this.progress = progress;
        Platform.runLater(this);
    }

    public void cancel() {
        isCancel = true;
    }

    private boolean isCancel = false;

}
