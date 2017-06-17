package downloadmanager.downloader;

import java.util.ArrayList;
import java.util.Collection;

import downloadmanager.File;

public class Downloader {

    private int mThreads;

    public void download(final File[] files, final int threadsCount) {
        final Collection<File> properList = new ArrayList<>();
        final Collection<File> corruptedList = new ArrayList<>();
        for (final File file :
                files) {
            if (file.isWritable) {
                properList.add(file);
            } else {
                corruptedList.add(file);
            }
        }
        mThreads = getThreads(threadsCount, properList.size());
    }

    private int getThreads(final int defaultValue, final int realValue) {
        if (defaultValue > realValue) {
            return realValue;
        } else {
            return defaultValue;
        }
    }
}
