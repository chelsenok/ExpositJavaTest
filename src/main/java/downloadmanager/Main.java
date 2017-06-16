package downloadmanager;

import java.util.Map;
import java.util.Scanner;

import downloadmanager.reader.ReadStrategy;
import downloadmanager.reader.ReadStrategyFactory;

import static downloadmanager.Main.OperationTypes.FILE_PATH;
import static downloadmanager.Main.OperationTypes.HELP;
import static downloadmanager.Main.OperationTypes.PATH;
import static downloadmanager.Main.OperationTypes.REFERENCE;
import static downloadmanager.Main.OperationTypes.THREADS;

public final class Main {

    private static final String APP_NAME = "downloadmanager";
    private static final String SUCCESS = "success";

    public interface OperationTypes {

        String PATH = "-p";
        String FILE_PATH = "-f";
        String HELP = "--help";
        String THREADS = "-t";
        String REFERENCE = "-l";
    }

    public enum DownloadDataType {
        File,
        SingleReference
    }

    public static void main(String... args) {
        /*!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!*/
        args = new String[]{"-p", "D:dijig", "-l", "http", "-t", "12"};
        /*!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!*/
        final ArgumentParser argumentParser = new ArgumentParser(args);
        final Map<String, String[]> hashMap = argumentParser.getArgumentedMap();
        final String message = getMapStatusMessage(hashMap);
        if (!message.equals(SUCCESS)) {
            exit(message);
        }
        final File[] files = getFiles(hashMap);
        final DownloadManager downloadManager = new DownloadManager();
        if (hashMap.containsKey(THREADS)) {
            downloadManager.download(files, Integer.valueOf(hashMap.get(THREADS)[0]));
        } else {
            downloadManager.download(files);
        }
    }

    private static File[] getFiles(final Map<String, String[]> hashMap) {
        final DownloadDataType downloadType = getDownloadType(hashMap);
        if (downloadType == DownloadDataType.SingleReference) {
            return new File[]{new File(hashMap.get(REFERENCE)[0], hashMap.get(PATH)[0])};
        } else if (downloadType == DownloadDataType.File) {
            final String path = hashMap.get(FILE_PATH)[0];
            final ReadStrategy readStrategy = getReaderStrategy(path);
            if (readStrategy == null) {
                exit(generateUnknownFileFormatMessage());
            }
            return readStrategy.readFiles(path);
        }
        return null;
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

    private static DownloadDataType getDownloadType(final Map<String, String[]> hashMap) {
        if (hashMap.get(REFERENCE).length != 0) {
            return DownloadDataType.SingleReference;
        }
        if (hashMap.get(FILE_PATH).length != 0) {
            return DownloadDataType.File;
        }
        return null;
    }

    private static String getMapStatusMessage(final Map<String, String[]> hashMap) {
        if (hashMap.isEmpty()) {
            return generateSyntaxErrorMessage();
        }
        if (hashMap.containsKey(HELP)) {
            if (hashMap.size() > 1 || hashMap.get(HELP).length != 0) {
                return generateSyntaxErrorMessage();
            } else {
                return generateHelpMessage();
            }
        }
        if ((!hashMap.containsKey(REFERENCE) && !hashMap.containsKey(FILE_PATH)) ||
                (hashMap.containsKey(REFERENCE) && hashMap.containsKey(FILE_PATH)
                        && hashMap.get(REFERENCE).length == 0 && hashMap.get(FILE_PATH).length == 0)) {
            return generateSyntaxErrorMessage();
        }
        if (hashMap.containsKey(REFERENCE) && hashMap.containsKey(FILE_PATH) &&
                hashMap.get(REFERENCE).length != 0 && hashMap.get(FILE_PATH).length != 0) {
            return generateLackOfDataMessage();
        }
        if (hashMap.containsKey(THREADS)) {
            if (hashMap.get(THREADS).length != 1) {
                return generateSyntaxErrorMessage();
            }
            Integer integer = null;
            try {
                integer = Integer.valueOf(hashMap.get(THREADS)[0]);
            } catch (final NumberFormatException ignored) {
            }
            if (integer == null || integer < 1) {
                return generateSyntaxErrorMessage();
            }
        }
        return SUCCESS;
    }

    private static String generateUnknownFileFormatMessage() {
        return "Unknown file format.\n" + generateTryHelpMessage();
    }

    private static String generateLackOfDataMessage() {
        return "Lack of data for downloading.\n" + generateTryHelpMessage();
    }

    private static String generateSyntaxErrorMessage() {
        return APP_NAME + ": missing operand\n" + generateTryHelpMessage();
    }

    private static String generateTryHelpMessage() {
        return "Try '" + APP_NAME + " --help' for more information.";
    }

    private static String generateHelpMessage() {
        return "Help information.";
    }

    private static void exit(final String message) {
        System.out.println(message);
        System.exit(1);
    }
}
