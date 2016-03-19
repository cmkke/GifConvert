package command.executor;

import java.text.NumberFormat;
import java.util.List;

public class CommandExecuteResult {

    protected final long costTime;

    protected final boolean success;

    protected final boolean canceled;

    protected final List<String> messages;

    public CommandExecuteResult(boolean success, boolean canceled, long costTime, List<String> messages) {
        this.success = success;
        this.canceled = canceled;
        this.costTime = costTime;
        this.messages = messages;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public boolean isSuccess() {
        return success;
    }

    public List<String> getMessages() {
        return messages;
    }

    public long getCostTime() {
        return costTime;
    }

}
