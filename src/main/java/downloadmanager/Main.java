package downloadmanager;

import java.util.Map;
import java.util.Scanner;

import downloadmanager.constants.DownloadDataTypes;
import downloadmanager.constants.OperationTypes;
import downloadmanager.constants.StatusMessages;
import downloadmanager.reader.ReadStrategy;
import downloadmanager.reader.ReadStrategyFactory;

public final class Main {

    private static final String APP_NAME = "downloadmanager";

    public static void main(String... args) {
        /*!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!*/
        args = new String[]{"-f", "file.json", "-p", "blabla"};
        /*!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!*/
        final ArgumentParser argumentParser = new ArgumentParser(args);
        final Map<String, String[]> hashMap = argumentParser.getArgumentedMap();
        final StatusMessages message = getMapStatusMessage(hashMap);
        if (message != (StatusMessages.Success)) {
            exit(message.getMessage(APP_NAME));
        }
        final DownloadDataTypes downloadType = getDownloadType(hashMap);
        final File[] files = getFiles(hashMap, downloadType);
        final DownloadManager downloadManager = new DownloadManager();
        if (hashMap.containsKey(OperationTypes.Threads.getType())) {
            downloadManager.download(files, Integer.valueOf(hashMap.get(OperationTypes.Threads.getType())[0]));
        } else {
            downloadManager.download(files);
        }
    }

    private static File[] getFiles(final Map<String, String[]> hashMap, final DownloadDataTypes downloadType) {
        if (downloadType == DownloadDataTypes.SingleReference) {
            return new File[]{new File(
                    hashMap.get(OperationTypes.Reference.getType())[0],
                    hashMap.get(OperationTypes.Path.getType())[0])};
        }
        if (downloadType == DownloadDataTypes.File) {
            final String path = hashMap.get(OperationTypes.FilePath.getType())[0];
            if (!fileExist(path)) {
                exit(StatusMessages.NonExistentFile.getMessage(APP_NAME));
            }
            final ReadStrategy readStrategy = getReaderStrategy(path);
            if (readStrategy == null) {
                exit(StatusMessages.UnknownFileFormat.getMessage(APP_NAME));
            }
            return readStrategy.readFiles(path);
        }
        return null;
    }

    private static boolean fileExist(final String path) {
        final java.io.File f = new java.io.File(path);
        return f.exists() && !f.isDirectory();
    }

    private static ReadStrategy getReaderStrategy(final String path) {
        String extension = "";
        final int i = path.lastIndexOf('.');
        if (i > 0) {
            extension = path.substring(i + 1);
        }
        return ReadStrategyFactory.getStrategy(
                extension,
                (message) -> {
                    System.out.print(message);
                    return new Scanner(System.in).next();
                }
        );
    }

    private static DownloadDataTypes getDownloadType(final Map<String, String[]> hashMap) {
        if (hashMap.containsKey(OperationTypes.Reference.getType())
                && hashMap.get(OperationTypes.Reference.getType()).length != 0) {
            return DownloadDataTypes.SingleReference;
        }
        if (hashMap.containsKey(OperationTypes.FilePath.getType())
                && hashMap.get(OperationTypes.FilePath.getType()).length != 0) {
            return DownloadDataTypes.File;
        }
        return null;
    }

    private static StatusMessages getMapStatusMessage(final Map<String, String[]> hashMap) {
        if (hashMap.isEmpty()) {
            return StatusMessages.SyntaxError;
        }
//        final boolean pathState = hashMap.containsKey(OperationTypes.Path.getType())
//                && hashMap.get(OperationTypes.Path.getType()).length == 1;
//        final boolean helpState = hashMap.containsKey(OperationTypes.Help.getType())
//                && hashMap.get(OperationTypes.Help.getType()).length == 1;
        if (hashMap.containsKey(OperationTypes.Help.getType())) {
            if (hashMap.size() > 1 || hashMap.get(OperationTypes.Help.getType()).length != 0) {
                return StatusMessages.SyntaxError;
            } else {
                return StatusMessages.Help;
            }
        }
        if (!hashMap.containsKey(OperationTypes.Path.getType())) {
            return StatusMessages.SyntaxError;
        }
        if ((!hashMap.containsKey(OperationTypes.Reference.getType()) &&
                !hashMap.containsKey(OperationTypes.FilePath.getType())) ||
                (hashMap.containsKey(OperationTypes.Reference.getType()) &&
                        hashMap.containsKey(OperationTypes.FilePath.getType()) &&
                        hashMap.get(OperationTypes.Reference.getType()).length == 0 &&
                        hashMap.get(OperationTypes.FilePath.getType()).length == 0) ||
                !hashMap.containsKey(OperationTypes.Path.getType())) {
            return StatusMessages.SyntaxError;
        }
        if (hashMap.containsKey(OperationTypes.Reference.getType()) && hashMap.containsKey(OperationTypes.FilePath.getType()) &&
                hashMap.get(OperationTypes.Reference.getType()).length != 0 && hashMap.get(OperationTypes.FilePath.getType()).length != 0) {
            return StatusMessages.LackOfData;
        }
        if (hashMap.containsKey(OperationTypes.Threads.getType())) {
            if (hashMap.get(OperationTypes.Threads.getType()).length != 1) {
                return StatusMessages.SyntaxError;
            }
            Integer integer = null;
            try {
                integer = Integer.valueOf(hashMap.get(OperationTypes.Threads.getType())[0]);
            } catch (final NumberFormatException ignored) {
            }
            if (integer == null || integer < 1) {
                return StatusMessages.SyntaxError;
            }
        }
        return StatusMessages.Success;
    }

    private static void exit(final String message) {
        System.out.println(message);
        System.exit(1);
    }
}
