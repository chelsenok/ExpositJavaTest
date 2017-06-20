package downloadmanager.progress;

public class ProgressWriter {

    private final String ANSI_RESET = "\u001B[0m";
    private final String ANSI_BLACK = "\u001B[30m";
    private final String ANSI_WHITE_BACKGROUND = "\u001B[47m";

    private final String SEPARATOR = "|";
    private final char FILL = ' ';
    private final String PROGRESS = "progress";
    private final int PROGRESS_WIDTH = 25;
    private final int DEFAULT_WIDTH = 11;
    private final String IN_PROCESS = "in process";
    private final String DOWNLOADED = "downloaded";
    private final String CORRUPTED = "corrupted";
    private final String TOTAL = "total";

    private double mProgress;
    private int mTotalFiles;
    private int mInProcessFiles;
    private int mDownloadedFiles;
    private int mCorruptedFiles;
    private int mCurrentFilledWidth;

    public void printDefaultFields() {
        System.out.println(SEPARATOR
                + PROGRESS + String.format("%" + (PROGRESS_WIDTH - PROGRESS.length()) + "s", FILL) + SEPARATOR
                + IN_PROCESS + String.format("%" + (DEFAULT_WIDTH - IN_PROCESS.length()) + "s", FILL) + SEPARATOR
                + DOWNLOADED + String.format("%" + (DEFAULT_WIDTH - DOWNLOADED.length()) + "s", FILL) + SEPARATOR
                + CORRUPTED + String.format("%" + (DEFAULT_WIDTH - CORRUPTED.length()) + "s", FILL) + SEPARATOR
                + TOTAL + String.format("%" + (DEFAULT_WIDTH - TOTAL.length()) + "s", FILL) + SEPARATOR
        );
    }

    private synchronized void updateConsole() {
        System.out.print('\r' + SEPARATOR);
        if (mCurrentFilledWidth != 0) {
            System.out.print(ANSI_WHITE_BACKGROUND + String.format("%" + mCurrentFilledWidth + "s", FILL));
        }
        if (mCurrentFilledWidth != PROGRESS_WIDTH) {
            System.out.print(ANSI_RESET + String.format("%" + (PROGRESS_WIDTH - mCurrentFilledWidth) + "s", FILL));
        }
        System.out.print(ANSI_RESET + SEPARATOR
                + getFilledValue(mInProcessFiles)
                + getFilledValue(mDownloadedFiles)
                + getFilledValue(mCorruptedFiles)
                + getFilledValue(mTotalFiles)
        );
    }

    private String getFilledValue(int value) {
        return String.format("%" + (DEFAULT_WIDTH - String.valueOf(value).length()) + "s", FILL)
                + value + SEPARATOR;
    }

    public void setProgress(double progress) {
        mProgress = progress;
        final int newValue = (int) (PROGRESS_WIDTH * mProgress);
        if (mCurrentFilledWidth != PROGRESS_WIDTH) {
            if (mCurrentFilledWidth != newValue) {
                mCurrentFilledWidth = newValue;
                updateConsole();
            }
        } else {
            System.out.print('\n');
        }
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
