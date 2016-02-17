package util;

import com.sun.istack.internal.NotNull;
import debug.Debug;
import javafx.application.Platform;

public class MessageTask implements Comparable<MessageTask> {

    private final Runnable preUiTask;
    private final Runnable task;
    private final Long timeRunAt;
    private final Object id;
    private final Exception trace = new Exception();

    public MessageTask(Runnable preUiTask, Runnable task, Object id, long delay) {
        this.preUiTask = preUiTask;
        this.task = task;
        this.id = id;
        this.timeRunAt = delay + System.currentTimeMillis();
    }

    public void run() {
        if (Debug.ENABLE) {
            trace.printStackTrace();
        }

        if (preUiTask != null) {
            Platform.runLater(preUiTask);
        }

        if (task != null) {
            task.run();
        }
    }

    public long getTimeRunAt() {
        return timeRunAt;
    }

    public Object getId() {
        return id;
    }

    @Override
    public int compareTo(@NotNull MessageTask o) {
        return timeRunAt.compareTo(o.timeRunAt);
    }

}
