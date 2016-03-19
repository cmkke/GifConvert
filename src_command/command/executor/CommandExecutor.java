package command.executor;

import debug.Debug;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CommandExecutor {

    private final Class loaderClass;

    private final String executorName;

    private final File executorFile;

    private Process executor;

    private boolean isCanceled;

    public CommandExecutor(Class loaderClass, String executorName) {
        this.loaderClass = loaderClass;
        this.executorName = executorName;
        executorFile = new File(System.getProperty("java.io.tmpdir"), executorName);
    }

    private void copyExecutorToTempDirectory() {
        try (OutputStream outputStream = new FileOutputStream(executorFile);
             InputStream inputStream = loaderClass.getResourceAsStream(executorName)) {
            byte[] buffer = new byte[4096];
            while (true) {
                int readCount = inputStream.read(buffer);
                if (readCount == -1) {
                    break;
                }

                outputStream.write(buffer, 0, readCount);
            }
            System.out.println("executor has copied to temp directory");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void ensureExecutorAvailable() {
        if (executorFile.exists()) {
            return;
        }

        copyExecutorToTempDirectory();
    }

    private final StringProperty executorStatus = new SimpleStringProperty();

    public StringProperty executorStatusProperty() {
        return executorStatus;
    }

    public CommandExecuteResult execute(CommandParameters commandParameters) {
        ensureExecutorAvailable();

        if (executor != null) {
            throw new RuntimeException("cannot execute two process together");
        }

        final long startTime = System.currentTimeMillis();
        try {
            List<String> command = new ArrayList<>();
            command.add(executorFile.getAbsolutePath());
            command.addAll(commandParameters.buildConvertCommand());
            ProcessBuilder processBuilder = new ProcessBuilder(command);
//            processBuilder.directory(executorFile.getParentFile());
            processBuilder.redirectErrorStream(true);
            executor = processBuilder.start();
            isCanceled = false;

            List<String> messages = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(executor.getInputStream()));
            while (true) {
                executorStatus.set(reader.readLine());
                if (executorStatus.get() == null) {
                    break;
                }

                if (Debug.ENABLE) {
                    System.out.println(executorStatus.get());
                }

                messages.add(executorStatus.get());
            }

            return new CommandExecuteResult(executor.waitFor() == 0, isCanceled, System.currentTimeMillis() - startTime, messages);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            executor = null;
        }

        return null;
    }

    public void cancel() {
        if (executor != null) {
            executor.destroy();
            isCanceled = true;
        }
    }

}
