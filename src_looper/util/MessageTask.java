package util;

import debug.Debug;
import javafx.application.Platform;

public abstract class MessageTask<R> extends Message {

    private final Exception trace = new Exception();

    public abstract void preTaskOnUi();

    public abstract R runTask();

    public abstract void postTaskOnUi(R result);

    @Override
    public void run() {
        if (Debug.ENABLE) {
            trace.printStackTrace();
        }

        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                preTaskOnUi();
            }

        });

        R result = runTask();

        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                postTaskOnUi(result);
            }

        });
    }

    public MessageTask(Object id, long delay) {
        super(id, delay);
    }

}
