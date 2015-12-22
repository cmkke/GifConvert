package getting.util;

import com.sun.istack.internal.NotNull;

public class Message implements Comparable<Message> {

    private final Runnable runnable;
    private final Long timeRunAt;
    private final Object id;

    public Runnable getRunnable() {
        return runnable;
    }

    public long getTimeRunAt() {
        return timeRunAt;
    }

    public Message(Runnable runnable, Object id, long delay) {
        this.runnable = runnable;
        this.id = id;
        this.timeRunAt = delay + System.currentTimeMillis();
    }

    public Object getId() {
        return id;
    }

    @Override
    public int compareTo(@NotNull Message o) {
        return timeRunAt.compareTo(o.timeRunAt);
    }

}
