package downloadmanager;

import org.jetbrains.annotations.Nullable;

import java.util.Scanner;

import downloadmanager.downloader.Downloader;
import downloadmanager.parser.ArgumentManager;
import downloadmanager.reader.Reader;
import downloadmanager.reader.ReaderFactory;

public final class Main {

    private static final String APP_NAME = "downloadmanager";

    public static void main(String... args) {
        /*!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!*/
        args = new String[]{"-f", "file.csv"};
        /*!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!*/
        final ArgumentManager argumentManager = new ArgumentManager(args);
        final StatusMessage message = getArgumentsStatusMessage(argumentManager);
        if (message != (StatusMessage.SUCCESS)) {
            exit(message.getMessage(APP_NAME));
        }
        final DownloadDataType downloadType = getDownloadType(argumentManager);
        final File[] files = getFiles(argumentManager, downloadType);
        new Downloader().download(files, argumentManager.threads);
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
                exit(StatusMessage.NON_EXISTENT_FILE.getMessage(APP_NAME));
            }
            final Reader reader = getReader(path);
            if (reader == null) {
                exit(StatusMessage.UNKNOWN_FILE_FORMAT.getMessage(APP_NAME));
            }
            return reader.readFiles(path, manager.downloadPath);
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
        return ReaderFactory.getStrategy(
                extension,
                (message) -> {
                    System.out.print(message);
                    return new Scanner(System.in).next();
                }
        );
    }

    private static DownloadDataType getDownloadType(final ArgumentManager manager) {
        if (manager.downloadPath != null) {
            return DownloadDataType.SINGLE_REFERENCE;
        }
        if (manager.filePath != null) {
            return DownloadDataType.FILE;
        }
        return null;
    }

    private static StatusMessage getArgumentsStatusMessage(final ArgumentManager manager) {
        if (!manager.isValid || (manager.reference == null && manager.filePath == null)) {
            return StatusMessage.SYNTAX_ERROR;
        }
        if (manager.needHelp) {
            return StatusMessage.HELP;
        }
        if (manager.reference != null && manager.filePath != null) {
            return StatusMessage.LACK_OF_DATA;
        }
        if (manager.threads < 1) {
            return StatusMessage.WRONG_THREAD_NUMBER;
        }
        if (manager.downloadPath != null && !new java.io.File(manager.downloadPath).canWrite()) {
            return StatusMessage.ACCESS_DENIED;
        }
        return StatusMessage.SUCCESS;
    }

    private static void exit(final String message) {
        System.out.println(message);
        System.exit(1);
    }
}
