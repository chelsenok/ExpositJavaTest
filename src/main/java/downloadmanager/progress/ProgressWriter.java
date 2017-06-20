package downloadmanager.progress;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import static downloadmanager.progress.TableField.CORRUPTED;
import static downloadmanager.progress.TableField.DOWNLOADED;
import static downloadmanager.progress.TableField.IN_PROCESS;
import static downloadmanager.progress.TableField.PROGRESS;
import static downloadmanager.progress.TableField.SPEED;
import static downloadmanager.progress.TableField.TOTAL;

public class ProgressWriter {

    private final int PERIOD = 1000;
    private final int MAX_PROGRESS_VALUE = 100;
    private final String SEPARATOR = "|";
    private final char FILL = ' ';
    private final int DEFAULT_WIDTH;
    private TimerTask mTimerTask;

    private double mProgress;
    private int mTotalFiles;
    private int mInProcessFiles;
    private int mDownloadedFiles;
    private int mCorruptedFiles;

    private final Timer mOutputThread;
    private double mSpeed;

    public ProgressWriter() {
        int maxLength = 0;
        for (TableField field :
                TableField.values()) {
            if (field.getName().length() > maxLength) {
                maxLength = field.getName().length();
            }
        }
        DEFAULT_WIDTH = maxLength + 1;
        mOutputThread = new Timer();
    }

    public void printDefaultFields() {
        System.out.println(SEPARATOR
                + PROGRESS.getName() + String.format("%" + (DEFAULT_WIDTH - PROGRESS.getName().length()) + "s", FILL) + SEPARATOR
                + IN_PROCESS.getName() + String.format("%" + (DEFAULT_WIDTH - IN_PROCESS.getName().length()) + "s", FILL) + SEPARATOR
                + DOWNLOADED.getName() + String.format("%" + (DEFAULT_WIDTH - DOWNLOADED.getName().length()) + "s", FILL) + SEPARATOR
                + CORRUPTED.getName() + String.format("%" + (DEFAULT_WIDTH - CORRUPTED.getName().length()) + "s", FILL) + SEPARATOR
                + TOTAL.getName() + String.format("%" + (DEFAULT_WIDTH - TOTAL.getName().length()) + "s", FILL) + SEPARATOR
                + SPEED.getName() + String.format("%" + (DEFAULT_WIDTH - SPEED.getName().length()) + "s", FILL) + SEPARATOR
        );
        mTimerTask = new TimerTask() {

            @Override
            public void run() {
                updateConsole();
            }
        };
        mOutputThread.schedule(mTimerTask, 0, PERIOD);
    }

    public void stopUpdating() {
        mOutputThread.cancel();
        mOutputThread.purge();
        mTimerTask.run();
        System.out.print('\n');
    }

    private synchronized void updateConsole() {
        System.out.print('\r' + SEPARATOR
                + getFilledValue(new DecimalFormat("0.00").format(mProgress) + '%')
                + getFilledValue(String.valueOf(mInProcessFiles))
                + getFilledValue(String.valueOf(mDownloadedFiles))
                + getFilledValue(String.valueOf(mCorruptedFiles))
                + getFilledValue(String.valueOf(mTotalFiles))
                + getFilledValue(new DecimalFormat("0.00").format(mSpeed) + "MB/s")
        );
    }

    private String getFilledValue(CharSequence value) {
        return String.format("%" + (DEFAULT_WIDTH - value.length()) + "s", FILL)
                + value + SEPARATOR;
    }

    public void setProgress(double progress) {
        mProgress = progress * MAX_PROGRESS_VALUE;
        if (mProgress == MAX_PROGRESS_VALUE) {
            stopUpdating();
        }
    }

    public void setTotalFiles(int totalFiles) {
        mTotalFiles = totalFiles;
    }

    public void setInProcessFiles(int inProcessFiles) {
        mInProcessFiles = inProcessFiles;
    }

    public void setDownloadedFiles(int downloadedFiles) {
        mDownloadedFiles = downloadedFiles;
    }

    public void setCorruptedFiles(int corruptedFiles) {
        mCorruptedFiles = corruptedFiles;
    }

    public void setSpeed(double speed) {
        mSpeed = speed;
    }
}
