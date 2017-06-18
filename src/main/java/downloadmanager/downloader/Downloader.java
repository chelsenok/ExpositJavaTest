package downloadmanager.downloader;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import downloadmanager.downloader.direct.DirectDownloader;
import downloadmanager.downloader.direct.DownloadListener;
import downloadmanager.downloader.direct.DownloadTask;

public class Downloader extends Observable implements Observer {

    private DirectDownloader mDirectDownloader;
    private int mFilePointer;
    private int mThreads;
    private double mProgress;
    private int mInProcess;
    private int mDownloaded;
    private int mCorrupted;
    private int mTotal;

    public void download(final File[] files, final int threadsCount) {
        final List<File> properList = new ArrayList<>();
        final List<File> corruptedList = new ArrayList<>();
        for (final File file :
                files) {
            if (file.isWritable) {
                properList.add(file);
            } else {
                corruptedList.add(file);
            }
        }
        mThreads = getThreads(threadsCount, properList.size());
        mCorrupted = corruptedList.size();
        mDirectDownloader = new DirectDownloader();
        for (mFilePointer = 0; mFilePointer < mThreads; mFilePointer++) {
            File file = properList.get(mFilePointer);
            try {
                mDirectDownloader.download(new DownloadTask(
                        new URL(file.link),
                        new FileOutputStream(file.path),
                        new DownloadListener() {

                            @Override
                            public void onStart(String fname, int fsize) {

                            }

                            @Override
                            public void onUpdate(int bytes, int totalDownloaded) {

                            }

                            @Override
                            public void onComplete() {

                            }

                            @Override
                            public void onCancel() {

                            }
                        }
                ));
            } catch (FileNotFoundException | MalformedURLException ignored) {
            }
        }
    }

    private int getThreads(final int defaultValue, final int realValue) {
        if (defaultValue > realValue) {
            return realValue;
        } else {
            return defaultValue;
        }
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
