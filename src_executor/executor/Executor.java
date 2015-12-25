package executor;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Executor {

    private final Class loaderClass;
    private final String executorName;
    private final File executorFile;

    public Executor(Class loaderClass, String executorName) {
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

    public void ensureExecutorAvailable() {
        if (executorFile.exists()) {
            System.out.println("executor exist");
            return;
        }

        copyExecutorToTempDirectory();
    }

    public ExecuteResult execute(String commandParameters) {
        ensureExecutorAvailable();

        final String command = executorFile.getAbsolutePath() + " " + commandParameters;
        System.out.println(command);
        try {
            Process converterProcess = Runtime.getRuntime().exec(command);

            List<String> messages = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(converterProcess.getErrorStream()));
            while (true) {
                String message = reader.readLine();
                if (message == null) {
                    break;
                }
                messages.add(message);
            }

            return new ExecuteResult(converterProcess.waitFor() == 0, messages);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return new ExecuteResult(false, null);
    }

}
