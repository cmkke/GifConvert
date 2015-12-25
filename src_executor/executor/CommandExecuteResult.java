package executor;

import java.util.List;

public class CommandExecuteResult {

    private final boolean success;
    private final List<String> message;

    public CommandExecuteResult(boolean success, List<String> message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public List<String> getMessage() {
        return message;
    }

}
