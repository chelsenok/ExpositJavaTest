package downloadmanager;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

import downloadmanager.downloader.ChangeType;
import downloadmanager.downloader.Downloader;
import downloadmanager.downloader.File;
import downloadmanager.downloader.FileAccessor;
import downloadmanager.parser.ArgumentManager;
import downloadmanager.progress.ProgressWriter;
import downloadmanager.reader.Reader;
import downloadmanager.reader.ReaderFactory;

public final class Main {

    private static final String NO_INTERNET_CONNECTION = "No Internet connection.";
    private static final String APP_NAME = "downloadmanager";
    private static final ProgressWriter sProgressWriter = new ProgressWriter();
    private static Downloader sDownloader;
    private static final Observer sObserver = new Observer() {

        @Override
        public synchronized void update(Observable o, Object arg) {
            switch ((ChangeType) arg) {
                case CORRUPTED_FILES:
                    sProgressWriter.setCorruptedFiles(sDownloader.getCorruptedFiles());
                    break;
                case DOWNLOAD_FILES:
                    sProgressWriter.setDownloadedFiles(sDownloader.getDownloadFiles());
                    break;
                case IN_PROCESS_FILES:
                    sProgressWriter.setInProcessFiles(sDownloader.getInProcessFiles());
                    break;
                case TOTAL_FILES:
                    sProgressWriter.setTotalFiles(sDownloader.getTotalFiles());
                    break;
                case DOWNLOADED_SIZE:
                    sProgressWriter.setProgress(sDownloader.getDownloadSize() / sDownloader.getTotalSize());
                    break;
            }
        }
    };

    public static void main(String... args) {
        /*!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!*/
        args = new String[]{"-l", "https://pp.userapi.com/c637720/v637720630/5fd3f/Umw4WJIKN9k.jpg",
                "-p", "/home/alexey/Education/a.jpg"};
        /*!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!*/
        final ArgumentManager argumentManager = new ArgumentManager(args);
        final ArgumentsStatusMessage message = getArgumentsStatusMessage(argumentManager);
        if (message != (ArgumentsStatusMessage.SUCCESS)) {
            exit(message.getMessage(APP_NAME));
        }
        if (!hasInternetConnection()) {
            exit(NO_INTERNET_CONNECTION);
        }
        final DownloadDataType downloadType = getDownloadType(argumentManager);
        final File[] files = getFiles(argumentManager, downloadType);
        sDownloader = new Downloader(files, argumentManager.threads);
        sDownloader.addObserver(sObserver);
        sDownloader.download();
        sProgressWriter.printDefaultFields();
    }

    private static @Nullable File[] getFiles(final ArgumentManager manager, final DownloadDataType downloadType) {
        if (downloadType == DownloadDataType.SINGLE_REFERENCE) {
            return new File[]{new File(
                    manager.reference,
                    manager.downloadPath
            )};
        }
        if (downloadType == DownloadDataType.FILE) {
            final String path = manager.filePath;
            if (!fileExist(path)) {
                exit(ArgumentsStatusMessage.NON_EXISTENT_FILE.getMessage(APP_NAME));
            }
            final Reader reader = getReader(path);
            if (reader == null) {
                exit(ArgumentsStatusMessage.UNKNOWN_FILE_FORMAT.getMessage(APP_NAME));
            }
            final String[][] content = reader.readContent(path);
            final File[] files = new File[content.length];
            for (int i = 0; i < content.length; i++) {
                files[i] = new File(content[i][0], manager.downloadPath + content[i][1]);
            }
            return files;
        }
        return null;
    }

    private static boolean fileExist(final String path) {
        final java.io.File f = new java.io.File(path);
        return f.exists() && !f.isDirectory() && f.canRead();
    }

    private static Reader getReader(final String path) {
        String extension = "";
        final int i = path.lastIndexOf('.');
        if (i > 0) {
            extension = path.substring(i + 1);
        }
        return ReaderFactory.getStrategy(extension);
    }

    private static DownloadDataType getDownloadType(final ArgumentManager manager) {
        if (manager.reference != null) {
            return DownloadDataType.SINGLE_REFERENCE;
        }
        if (manager.filePath != null) {
            return DownloadDataType.FILE;
        }
        return null;
    }

    private static ArgumentsStatusMessage getArgumentsStatusMessage(final ArgumentManager manager) {
        if (!manager.isValid || (manager.reference == null && manager.filePath == null)) {
            return ArgumentsStatusMessage.SYNTAX_ERROR;
        }
        if (manager.needHelp) {
            return ArgumentsStatusMessage.HELP;
        }
        if (manager.reference != null && manager.filePath != null) {
            return ArgumentsStatusMessage.LACK_OF_DATA;
        }
        if (manager.threads < 1) {
            return ArgumentsStatusMessage.WRONG_THREAD_NUMBER;
        }
        if (manager.downloadPath != null && !FileAccessor.isWritable(manager.downloadPath)) {
            return ArgumentsStatusMessage.ACCESS_DENIED;
        }
        return ArgumentsStatusMessage.SUCCESS;
    }

    private static boolean hasInternetConnection() {
        final int timeout = 3000;
        final int port = 80;
        Socket sock = new Socket();
        InetSocketAddress addr = new InetSocketAddress("google.com", port);
        try {
            sock.connect(addr, timeout);
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            try {
                sock.close();
            } catch (IOException ignored) {
            }
        }
    }

    private static void exit(final String message) {
        System.out.println(message);
        System.exit(1);
    }
}
