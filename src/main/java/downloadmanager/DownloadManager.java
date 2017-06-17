package downloadmanager;

public class DownloadManager {

    void download(final File[] files, final int threadsCount) {
        if (files == null) {
            throw new NullPointerException();
        }
    }

    void download(final File[] files) {
        if (files == null) {
            throw new NullPointerException();
        }
    }
}
