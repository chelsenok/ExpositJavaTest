package downloadmanager.downloader;

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

import static downloadmanager.downloader.AccessRight.FILE_EXIST;
import static downloadmanager.downloader.ChangeType.CORRUPTED_FILES;
import static downloadmanager.downloader.ChangeType.DOWNLOADED_SIZE;
import static downloadmanager.downloader.ChangeType.DOWNLOAD_FILES;
import static downloadmanager.downloader.ChangeType.IN_PROCESS_FILES;
import static downloadmanager.downloader.ChangeType.THREADS_COUNT;
import static downloadmanager.downloader.ChangeType.TOTAL_FILES;
import static downloadmanager.downloader.ChangeType.TOTAL_SIZE;

public class Downloader extends Observable {

    private final int mThreads;
    private final File[] mProperFiles;
    private final File[] mCorruptedFiles;
    private final int mTotalSize;
    private int mDownloadedSize;
    private final DirectDownloader mDirectDownloader;
    private int mFilePointer;
    private int mInProcessFiles;
    private int mDownloadedFiles;

    public Downloader(final File[] files, int threads) {
        final List<File> properList = new ArrayList<>();
        final List<File> corruptedList = new ArrayList<>();
        distributeFiles(files, properList, corruptedList);
        mThreads = getThreads(threads, properList.size());
        if (mThreads != threads) {
            notifyObservers(THREADS_COUNT);
        }
        mCorruptedFiles = corruptedList.toArray(new File[corruptedList.size()]);
        notifyObservers(CORRUPTED_FILES);
        mProperFiles = properList.toArray(new File[properList.size()]);
        notifyObservers(TOTAL_FILES);
        mTotalSize = calculateTotalSize(mProperFiles);
        notifyObservers(TOTAL_SIZE);
        mDirectDownloader = new DirectDownloader();
    }

    private void distributeFiles(File[] files, Collection<File> properList, Collection<File> corruptedList) {
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
        for (mFilePointer = 0; mFilePointer < mThreads; mFilePointer++) {
            download(mFilePointer);
        }
    }

    private DownloadListener getDownloadListener() {
        return new DownloadListener() {

            @Override
            public void onStart(String fname, int fsize) {
                mInProcessFiles++;
                notifyObservers(IN_PROCESS_FILES);
            }

            @Override
            public void onUpdate(int bytes, int totalDownloaded) {
                mDownloadedSize += bytes;
                notifyObservers(DOWNLOADED_SIZE);
            }

            @Override
            public void onComplete() {
                mDownloadedFiles++;
                notifyObservers(DOWNLOAD_FILES);
                mInProcessFiles--;
                notifyObservers(IN_PROCESS_FILES);
                download(mFilePointer);
                ++mFilePointer;
            }

            @Override
            public void onCancel() {
                mInProcessFiles--;
                notifyObservers(IN_PROCESS_FILES);
                download(mFilePointer);
                ++mFilePointer;
            }
        };
    }

    private void download(int index) {
        if (index >= mProperFiles.length) {
            return;
        }
        File file = mProperFiles[mFilePointer];
        final String realPath = createFile(file.path);
        try {
            mDirectDownloader.download(new DownloadTask(
                    new URL(file.link),
                    new FileOutputStream(realPath),
                    getDownloadListener()
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
        } catch (IOException ignored) {
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

    private int getFileSize(URL url) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            conn.getInputStream();
            return conn.getContentLength();
        } catch (IOException e) {
            return -1;
        } finally {
            conn.disconnect();
        }
    }

    private int calculateTotalSize(File[] properFiles) {
        int size = 0;
        for (File file :
                properFiles) {
            try {
                size += getFileSize(new URL(file.link));
            } catch (MalformedURLException ignored) {
            }
        }
        return size;
    }

    @Override
    public void notifyObservers(Object arg) {
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
        return getCorruptedFiles() + getDownloadFiles() + getInProcessFiles();
    }

    public int getTotalSize() {
        return mTotalSize;
    }

}
