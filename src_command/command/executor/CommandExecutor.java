package command.executor;

import debug.Debug;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandExecutor {

    private final Class loaderClass;

    private final String executorName;

    private final File executorFile;

    public CommandExecutor(Class loaderClass, String executorName) {
        this.loaderClass = loaderClass;
        this.executorName = executorName;
        executorFile = new File(System.getProperty("java.io.tmpdir"), executorName);
    }

    private void copyExecutorToTempDirectory() {
        try {
            OutputStream outputStream = new FileOutputStream(executorFile);
            InputStream inputStream = loaderClass.getResourceAsStream(executorName);
            byte[] buffer = new byte[4096];
            while (true) {
                int readCount = inputStream.read(buffer);
                if (readCount == -1) {
                    break;
                }

                outputStream.write(buffer, 0, readCount);
            }
            inputStream.close();
            outputStream.close();
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

    private Process currentProcess;

    public CommandExecuteResult execute(CommandParameters commandParameters) {
        return execute(commandParameters, null);
    }

    public CommandExecuteResult execute(CommandParameters commandParameters, ObservableList<String> processStatus) {
        ensureExecutorAvailable();

        if (currentProcess != null) {
            throw new RuntimeException("cannot execute two process together");
        }

        if (processStatus == null) {
            processStatus = FXCollections.observableArrayList();
        }

        final long startTime = System.currentTimeMillis();
        try {
            List<String> command = new ArrayList<>();
            command.add(executorFile.getAbsolutePath());
            command.addAll(commandParameters.buildConvertCommand());
            ProcessBuilder processBuilder = new ProcessBuilder(command);
//            processBuilder.directory(executorFile.getParentFile());
            processBuilder.redirectErrorStream(true);
            currentProcess = processBuilder.start();
            isCanceled = false;

            BufferedReader reader = new BufferedReader(new InputStreamReader(currentProcess.getInputStream()));
            while (true) {
                String message = reader.readLine();
                if (message == null) {
                    break;
                }

                if (Debug.ENABLE) {
                    System.out.println(message);
                }

                processStatus.add(message);
            }

            return new CommandExecuteResult(currentProcess.waitFor() == 0,
                    isCanceled,
                    System.currentTimeMillis() - startTime,
                    Arrays.asList(processStatus.toArray(new String[processStatus.size()])));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            currentProcess = null;
        }

        return null;
    }

    public void cancel() {
        if (currentProcess != null) {
            currentProcess.destroy();
            isCanceled = true;
        }
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    private boolean isCanceled;

}
