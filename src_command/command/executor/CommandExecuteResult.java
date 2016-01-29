package command.executor;

import java.text.NumberFormat;
import java.util.List;

public class CommandExecuteResult {

    private final long costTime;

    private final boolean success;

    private final List<String> messages;

    public CommandExecuteResult(boolean success, long costTime, List<String> messages) {
        this.success = success;
        this.costTime = costTime;
        this.messages = messages;
    }

    public boolean isSuccess() {
        return success;
    }

    public List<String> getMessages() {
        return messages;
    }

    public String getCostTimeString() {
        return NumberFormat.getNumberInstance().format(costTime / 1000.0) + " 秒";
    }

    public long getCostTime() {
        return costTime;
    }

}
