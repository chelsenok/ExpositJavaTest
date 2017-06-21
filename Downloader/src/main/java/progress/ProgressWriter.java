package progress;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static progress.TableField.CORRUPTED;
import static progress.TableField.DOWNLOADED;
import static progress.TableField.IN_PROCESS;
import static progress.TableField.PROGRESS;
import static progress.TableField.SPEED;
import static progress.TableField.TIME_LEFT;
import static progress.TableField.TOTAL;

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
    private int mTimeLeft;

    public ProgressWriter() {
        int maxLength = 0;
        for (final TableField field :
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
                + PROGRESS.getName() + getFilledString(PROGRESS.getName()) + SEPARATOR
                + IN_PROCESS.getName() + getFilledString(IN_PROCESS.getName()) + SEPARATOR
                + DOWNLOADED.getName() + getFilledString(DOWNLOADED.getName()) + SEPARATOR
                + CORRUPTED.getName() + getFilledString(CORRUPTED.getName()) + SEPARATOR
                + TOTAL.getName() + getFilledString(TOTAL.getName()) + SEPARATOR
                + SPEED.getName() + getFilledString(SPEED.getName()) + SEPARATOR
                + TIME_LEFT.getName() + getFilledString(TIME_LEFT.getName()) + SEPARATOR
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
        try {
            Thread.sleep(PERIOD);
        } catch (final InterruptedException ignored) {
        }
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
                + getFilledValue(
                String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(mTimeLeft),
                        TimeUnit.MILLISECONDS.toMinutes(mTimeLeft) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(mTimeLeft)),
                        TimeUnit.MILLISECONDS.toSeconds(mTimeLeft) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mTimeLeft))))
        );
    }

    private String getFilledValue(final CharSequence value) {
        return getFilledString(value) + value + SEPARATOR;
    }

    private String getFilledString(final CharSequence value) {
        return new String(new char[DEFAULT_WIDTH - value.length()]).replace('\0', FILL);
    }

    public void setProgress(final double progress) {
        mProgress = progress * MAX_PROGRESS_VALUE;
        if (mProgress == MAX_PROGRESS_VALUE) {
            stopUpdating();
        }
    }

    public void setTotalFiles(final int totalFiles) {
        mTotalFiles = totalFiles;
    }

    public void setInProcessFiles(final int inProcessFiles) {
        mInProcessFiles = inProcessFiles;
    }

    public void setDownloadedFiles(final int downloadedFiles) {
        mDownloadedFiles = downloadedFiles;
    }

    public void setCorruptedFiles(final int corruptedFiles) {
        mCorruptedFiles = corruptedFiles;
    }

    public void setSpeed(final double speed) {
        mSpeed = speed;
    }

    public void setTimeLeft(final int timeLeft) {
        mTimeLeft = timeLeft;
    }
}
