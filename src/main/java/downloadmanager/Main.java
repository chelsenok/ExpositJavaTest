package downloadmanager;

import org.jetbrains.annotations.Nullable;

import java.util.Scanner;

import downloadmanager.downloader.Downloader;
import downloadmanager.file.File;
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
        final StatusMessages message = getArgumentsStatusMessage(argumentManager);
        if (message != (StatusMessages.Success)) {
            exit(message.getMessage(APP_NAME));
        }
        final DownloadDataTypes downloadType = getDownloadType(argumentManager);
        final File[] files = getFiles(argumentManager, downloadType);
        final Downloader downloader = new Downloader();
        downloader.download(files, argumentManager.threads);
    }

    private static @Nullable File[] getFiles(final ArgumentManager manager, final DownloadDataTypes downloadType) {
        if (downloadType == DownloadDataTypes.SingleReference) {
            return new File[]{new File(
                    manager.reference,
                    manager.downloadPath
            )};
        }
        if (downloadType == DownloadDataTypes.File) {
            final String path = manager.filePath;
            if (!fileExist(path)) {
                exit(StatusMessages.NonExistentFile.getMessage(APP_NAME));
            }
            final Reader reader = getReader(path);
            if (reader == null) {
                exit(StatusMessages.UnknownFileFormat.getMessage(APP_NAME));
            }
            return reader.readFiles(path);
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

    private static DownloadDataTypes getDownloadType(final ArgumentManager manager) {
        if (manager.downloadPath != null) {
            return DownloadDataTypes.SingleReference;
        }
        if (manager.filePath != null) {
            return DownloadDataTypes.File;
        }
        return null;
    }

    private static StatusMessages getArgumentsStatusMessage(final ArgumentManager manager) {
        if (!manager.isValid || (manager.reference == null && manager.filePath == null)) {
            return StatusMessages.SyntaxError;
        }
        if (manager.needHelp) {
            return StatusMessages.Help;
        }
        if (manager.reference != null && manager.filePath != null) {
            return StatusMessages.LackOfData;
        }
        if (manager.threads < 1) {
            return StatusMessages.WrongThreadNumber;
        }
        if (manager.downloadPath != null && !new java.io.File(manager.downloadPath).canWrite()) {
            return StatusMessages.AccessDenied;
        }
        return StatusMessages.Success;
    }

    private static void exit(final String message) {
        System.out.println(message);
        System.exit(1);
    }
}
