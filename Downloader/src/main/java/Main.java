import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Observer;

import downloader.ChangeType;
import downloader.Downloader;
import downloader.File;
import downloader.filemanager.AccessRight;
import downloader.filemanager.FileAccessor;
import parser.ArgumentManager;
import progress.ProgressWriter;
import reader.Reader;
import reader.ReaderFactory;

public final class Main {

    private static final String NO_INTERNET_CONNECTION = "No Internet connection.";
    private static final String APP_NAME = "downloadmanager";
    private static final ProgressWriter sProgressWriter = new ProgressWriter();
    private static Downloader sDownloader;
    private static final Observer sObserver = (o, arg) -> {
        if (arg != null) {
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
                    sProgressWriter.setProgress(sDownloader.getDownloadSize() / (double) sDownloader.getTotalSize());
                    break;
                case NOTHING_TO_DOWNLOAD:
                    sProgressWriter.stopUpdating();
                    break;
                case DOWNLOAD_SPEED:
                    sProgressWriter.setSpeed(sDownloader.getSpeed());
                    break;
                case TIME_LEFT:
                    sProgressWriter.setTimeLeft(sDownloader.getTimeLeft());
                    break;
            }
        }
    };

    public static void main(final String... args) {
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
        sProgressWriter.printDefaultFields();
        sDownloader.download();
    }

    private static @Nullable File[] getFiles(final ArgumentManager manager, final DownloadDataType downloadType) {
        @Nullable String clearPath;
        try {
            clearPath = manager.downloadPath.replace("\\", "/");
        } catch (final NullPointerException ignored) {
            clearPath = null;
        }
        if (downloadType == DownloadDataType.SINGLE_REFERENCE) {
            return new File[]{new File(
                    manager.reference,
                    clearPath
            )};
        }
        if (downloadType == DownloadDataType.FILE) {
            final String path = manager.filePath;
            if (!FileAccessor.fileExist(path)) {
                exit(ArgumentsStatusMessage.NON_EXISTENT_FILE.getMessage(APP_NAME));
            }
            final Reader reader = ReaderFactory.getStrategy(FileAccessor.getFileExtension(path));
            if (reader == null) {
                exit(ArgumentsStatusMessage.UNKNOWN_FILE_FORMAT.getMessage(APP_NAME));
            }
            final String[][] content = reader.readContent(path);
            final File[] files = new File[content.length];
            for (int i = 0; i < content.length; i++) {
                final String p;
                if (clearPath == null) {
                    p = "";
                } else {
                    if (clearPath.charAt(clearPath.length() - 1) != '/') {
                        p = clearPath + '/';
                    } else {
                        p = clearPath;
                    }
                }
                files[i] = new File(content[i][0], p + content[i][1]);
            }
            return files;
        }
        return null;
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
        if (manager.isValid && manager.needHelp) {
            return ArgumentsStatusMessage.HELP;
        }
        if (!manager.isValid || (manager.reference == null && manager.filePath == null)) {
            return ArgumentsStatusMessage.SYNTAX_ERROR;
        }
        if (manager.reference != null && manager.filePath != null) {
            return ArgumentsStatusMessage.LACK_OF_DATA;
        }
        if (manager.threads < 1) {
            return ArgumentsStatusMessage.WRONG_THREAD_NUMBER;
        }
        if (manager.downloadPath != null && FileAccessor.getAccessRight(manager.downloadPath) == AccessRight.DENIED) {
            return ArgumentsStatusMessage.ACCESS_DENIED;
        }
        return ArgumentsStatusMessage.SUCCESS;
    }

    private static boolean hasInternetConnection() {
        final int timeout = 3000;
        final int port = 80;
        final Socket sock = new Socket();
        final InetSocketAddress addr = new InetSocketAddress("google.com", port);
        try {
            sock.connect(addr, timeout);
            return true;
        } catch (final IOException e) {
            return false;
        } finally {
            try {
                sock.close();
            } catch (final IOException ignored) {
            }
        }
    }

    private static void exit(final String message) {
        System.out.println(message);
        System.exit(1);
    }
}
