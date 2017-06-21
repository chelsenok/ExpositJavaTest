package downloader;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

import static downloader.AccessRight.FILE_EXIST;
import static downloader.ChangeType.CORRUPTED_FILES;
import static downloader.ChangeType.DOWNLOADED_SIZE;
import static downloader.ChangeType.DOWNLOAD_FILES;
import static downloader.ChangeType.DOWNLOAD_SPEED;
import static downloader.ChangeType.IN_PROCESS_FILES;
import static downloader.ChangeType.NOTHING_TO_DOWNLOAD;
import static downloader.ChangeType.THREADS_COUNT;
import static downloader.ChangeType.TIME_LEFT;
import static downloader.ChangeType.TOTAL_FILES;
import static downloader.ChangeType.TOTAL_SIZE;

public class Downloader extends Observable {

    private final int mThreads;
    private final int PERIOD = 1000;
    private final File[] mProperFiles;
    private final File[] mCorruptedFiles;
    private final int mTotalSize;
    private int mDownloadedSize;
    private final DirectDownloader mDirectDownloader;
    private int mFilePointer;
    private int mInProcessFiles;
    private int mDownloadedFiles;
    private int mTimeLeft;
    private int mDownloadedSessionSize;
    private double mDownloadedSessionSpeed;
    private Timer mTimer;
    private final DownloadListener mDownloadListener = new DownloadListener() {

        @Override
        public void onStart(final String fname, final int fsize) {
            mInProcessFiles++;
            notifyObservers(IN_PROCESS_FILES);
        }

        @Override
        public void onUpdate(final int bytes, final int totalDownloaded) {
            mDownloadedSize += bytes;
            mDownloadedSessionSize += bytes;
            if (mDownloadedSize < mTotalSize) {
                notifyObservers(DOWNLOADED_SIZE);
            }
        }

        @Override
        public void onComplete() {
            mDownloadedFiles++;
            notifyObservers(DOWNLOAD_FILES);
            mInProcessFiles--;
            notifyObservers(IN_PROCESS_FILES);
            download(mFilePointer);
            ++mFilePointer;
            if (checkFinish()) {
                finish();
            }
        }

        @Override
        public void onCancel() {
            mInProcessFiles--;
            notifyObservers(IN_PROCESS_FILES);
            download(mFilePointer);
            ++mFilePointer;
            if (checkFinish()) {
                finish();
            }
        }
    };

    private void finish() {
        mDownloadedSize = mTotalSize;
        mTimer.cancel();
        mTimer.purge();
        notifyObservers(DOWNLOADED_SIZE);
    }

    public Downloader(final File[] files, final int threads) {
        final List<File> properList = new ArrayList<>();
        final List<File> corruptedList = new ArrayList<>();
        distributeFiles(files, properList, corruptedList);
        mThreads = getThreads(threads, properList.size());
        mCorruptedFiles = corruptedList.toArray(new File[corruptedList.size()]);
        mProperFiles = properList.toArray(new File[properList.size()]);
        mTotalSize = calculateTotalSize(mProperFiles);
        mDirectDownloader = new DirectDownloader();
    }

    private void distributeFiles(final File[] files, final Collection<File> properList, final Collection<File> corruptedList) {
        for (final File file :
                files) {
            if (file.isWritable) {
                properList.add(file);
            } else {
                corruptedList.add(file);
            }
        }
    }

    public void download() {
        notifyObservers(CORRUPTED_FILES);
        notifyObservers(TOTAL_FILES);
        notifyObservers(THREADS_COUNT);
        notifyObservers(TOTAL_SIZE);
        if (checkFinish()) {
            notifyObservers(NOTHING_TO_DOWNLOAD);
            return;
        }
        mTimer = new Timer();
        mTimer.schedule(
                new TimerTask() {

                    @Override
                    public void run() {
                        mDownloadedSessionSpeed = (double) mDownloadedSessionSize / (PERIOD * 1000);
                        notifyObservers(DOWNLOAD_SPEED);
                        mTimeLeft = (mTotalSize - mDownloadedSize) / (mDownloadedSessionSize / PERIOD);
                        notifyObservers(TIME_LEFT);
                        mDownloadedSessionSize = 0;
                    }
                },
                PERIOD,
                PERIOD
        );
        for (mFilePointer = 0; mFilePointer < mThreads; mFilePointer++) {
            download(mFilePointer);
        }
    }

    private boolean checkFinish() {
        return getDownloadFiles() + getCorruptedFiles() == getTotalFiles();
    }

    private void download(final int index) {
        if (index >= mProperFiles.length) {
            return;
        }
        final File file = mProperFiles[mFilePointer];
        final String realPath = createFile(file.path);
        try {
            mDirectDownloader.download(new DownloadTask(
                    new URL(file.link),
                    new FileOutputStream(realPath),
                    mDownloadListener
            ));
        } catch (FileNotFoundException | MalformedURLException ignored) {
        }
    }

    private String createFile(final String path) {
        try {
            int index = 1;
            String realPath = path;
            while (FileAccessor.getAccessRight(realPath) == FILE_EXIST) {
                realPath = FileAccessor.addFileIndex(path, index);
                index++;
            }
            new java.io.File(path).createNewFile();
            return realPath;
        } catch (final IOException ignored) {
            return "";
        }
    }

    private int getThreads(final int defaultValue, final int realValue) {
        if (defaultValue > realValue) {
            return realValue;
        } else {
            return defaultValue;
        }
    }

    static int getFileSize(final String link) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(link).openConnection();
            conn.setRequestMethod("HEAD");
            conn.getInputStream();
            return conn.getContentLength();
        } catch (final IOException e) {
            return -1;
        } finally {
            try {
                conn.disconnect();
            } catch (final NullPointerException ignored) {
            }
        }
    }

    static String getFileName(final String link) {
        HttpURLConnection conn = null;
        try {
            final URL url = new URL(link);
            conn = (HttpURLConnection) url.openConnection();
            final String fieldValue = conn.getHeaderField("Content-Disposition");
            if (fieldValue == null || !fieldValue.contains("filename=\"")) {
                throw new IOException();
            }
            return fieldValue.substring(fieldValue.indexOf("filename=\"") + 10, fieldValue.length() - 1);
        } catch (final IOException e) {
            return null;
        } finally {
            conn.disconnect();
        }
    }

    private int calculateTotalSize(final File[] properFiles) {
        int size = 0;
        for (final File file :
                properFiles) {
            size += getFileSize(file.link);
        }
        return size;
    }

    @Override
    public synchronized void notifyObservers(final Object arg) {
        setChanged();
        super.notifyObservers(arg);
    }

    public int getThreadsCount() {
        return mThreads;
    }

    public int getDownloadFiles() {
        return mDownloadedFiles;
    }

    public int getDownloadSize() {
        return mDownloadedSize;
    }

    public int getCorruptedFiles() {
        return mCorruptedFiles.length;
    }

    public int getInProcessFiles() {
        return mInProcessFiles;
    }

    public int getTotalFiles() {
        return mProperFiles.length + mCorruptedFiles.length;
    }

    public int getTotalSize() {
        return mTotalSize;
    }

    public double getSpeed() {
        return mDownloadedSessionSpeed;
    }

    public int getTimeLeft() {
        return mTimeLeft;
    }

}
