package util;

import com.sun.istack.internal.NotNull;
import javafx.application.Platform;

public class Message implements Comparable<Message> {

    private final Runnable runnable;
    private final Long timeRunAt;
    private final Object id;
    private final boolean shouldRunOnUiThread;

    public Message(Runnable runnable, Object id, long delay) {
        this(runnable, id, delay, false);
    }

    public Message(Runnable runnable, Object id, long delay, boolean shouldRunOnUiThread) {
        this.runnable = runnable;
        this.id = id;
        this.timeRunAt = delay + System.currentTimeMillis();
        this.shouldRunOnUiThread = shouldRunOnUiThread;
    }

    public void run() {
        if (shouldRunOnUiThread) {
            Platform.runLater(runnable);
        } else {
            runnable.run();
        }
    }

    public long getTimeRunAt() {
        return timeRunAt;
    }

    public Object getId() {
        return id;
    }

    @Override
    public int compareTo(@NotNull Message o) {
        return timeRunAt.compareTo(o.timeRunAt);
    }

}
