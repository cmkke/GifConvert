package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Looper {

    private final static ThreadLocal<Looper> LOOPER_THREAD_LOCAL = new ThreadLocal<>();
    private final Object lock = new Object();
    private final List<MessageTask> messageTasks = new ArrayList<>();

    private Looper() {
    }

    public static void prepare() {
        if (LOOPER_THREAD_LOCAL.get() != null) {
            throw new IllegalStateException("can not call Looper.prepare() twice");
        }

        LOOPER_THREAD_LOCAL.set(new Looper());
        loop();
    }

    private static void loop() {
        final Looper looper = myLoop();
        Thread thread = new Thread() {

            @Override
            public void run() {
                try {
                    while (true) {
                        MessageTask messageTask;
                        synchronized (looper.lock) {

                            if (looper.messageTasks.isEmpty()) {
                                looper.lock.wait();
                                continue;
                            }

                            final long waitTime = looper.messageTasks.get(0).getTimeRunAt() - System.currentTimeMillis();
                            if (waitTime > 0) {
                                looper.lock.wait(waitTime);
                                continue;
                            }


                            messageTask = looper.messageTasks.remove(0);
                        }

                        messageTask.run();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        };
        thread.setDaemon(true);
        thread.start();
    }

    public static void postMessage(MessageTask messageTask) {
        final Looper looper = myLoop();
        synchronized (looper.lock) {
            looper.messageTasks.add(messageTask);
            Collections.sort(looper.messageTasks);
            looper.lock.notifyAll();
        }
    }

    public static void removeMessage(Object id) {
        final Looper looper = myLoop();
        synchronized (looper.lock) {
            Iterator<MessageTask> iterator = looper.messageTasks.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().getId() == id) {
                    iterator.remove();
                }
            }

            looper.lock.notifyAll();
        }
    }

    public static void removeAllMessage() {
        final Looper looper = myLoop();
        synchronized (looper.lock) {
            looper.messageTasks.clear();
            looper.lock.notifyAll();
        }
    }

    private static Looper myLoop() {
        if (LOOPER_THREAD_LOCAL.get() == null) {
            throw new IllegalStateException("call Looper.prepare() first");
        }

        return LOOPER_THREAD_LOCAL.get();
    }

}
