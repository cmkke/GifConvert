package util;

import com.sun.istack.internal.NotNull;

public abstract class Message implements Comparable<Message> {

    private final Long timeRunAt;
    private final Object id;

    public Message(Object id, long delay) {
        this.id = id;
        this.timeRunAt = delay + System.currentTimeMillis();
    }

    public abstract void run();

    public abstract void cancel();

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
