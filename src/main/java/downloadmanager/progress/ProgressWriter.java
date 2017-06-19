package downloadmanager.progress;

public class ProgressWriter {

    private final String SEPARATOR = "|";
    private final char FILL = ' ';
    private final String PROGRESS = "progress";
    private final int PROGRESS_WIDTH = 25;
    private final int DEFAULT_WIDTH = 10;
    private final String IN_PROCESS = "in process";
    private final String DOWNLOADED = "downloaded";
    private final String CORRUPTED = "corrupted";
    private final String TOTAL = "total";

    private double mProgress;
    private int mTotalFiles;
    private int mInProcessFiles;
    private int mDownloadedFiles;
    private int mCorruptedFiles;

    public void printDefaultFields() {
        System.out.println(SEPARATOR
                + PROGRESS + String.format("%" + (PROGRESS_WIDTH - PROGRESS.length()) + "s", FILL) + SEPARATOR
                + IN_PROCESS + String.format("%" + (DEFAULT_WIDTH - IN_PROCESS.length()) + "s", FILL) + SEPARATOR
                + DOWNLOADED + String.format("%" + (DEFAULT_WIDTH - DOWNLOADED.length()) + "s", FILL) + SEPARATOR
                + CORRUPTED + String.format("%" + (DEFAULT_WIDTH - CORRUPTED.length()) + "s", FILL)  + SEPARATOR
                + TOTAL + String.format("%" + (DEFAULT_WIDTH - TOTAL.length()) + "s", FILL) + SEPARATOR
        );
    }

    private void updateConsole() {

    }

    public void setProgress(double progress) {
        mProgress = progress;
        updateConsole();
    }

    public void setTotalFiles(int totalFiles) {
        mTotalFiles = totalFiles;
        updateConsole();
    }

    public void setInProcessFiles(int inProcessFiles) {
        mInProcessFiles = inProcessFiles;
        updateConsole();
    }

    public void setDownloadedFiles(int downloadedFiles) {
        mDownloadedFiles = downloadedFiles;
        updateConsole();
    }

    public void setCorruptedFiles(int corruptedFiles) {
        mCorruptedFiles = corruptedFiles;
        updateConsole();
    }
}
