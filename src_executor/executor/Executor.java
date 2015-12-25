package executor;

import java.io.*;

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

    public boolean execute(String commandParameters) {
        ensureExecutorAvailable();

        final String command = executorFile.getAbsolutePath() + " " + commandParameters;
        System.out.println(command);
        try {
            Process converterProcess = Runtime.getRuntime().exec(command);
            return converterProcess.waitFor() == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

}
