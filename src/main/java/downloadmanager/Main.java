package downloadmanager;

import java.util.Map;
import java.util.Scanner;

import downloadmanager.readstrategy.ReadStrategy;
import downloadmanager.readstrategy.ReadStrategyFactory;

import static downloadmanager.Main.OperationTypes.FILE;
import static downloadmanager.Main.OperationTypes.HELP;
import static downloadmanager.Main.OperationTypes.PATH;
import static downloadmanager.Main.OperationTypes.REFERENCE;

public final class Main {

    private static final String APP_NAME = "downloadmanager";
    private static final String SUCCESS = "success";

    public interface OperationTypes {

        String PATH = "-p";
        String FILE = "-f";
        String HELP = "--help";
        String THREADS = "-t";
        String REFERENCE = "-l";
    }

    public enum DownloadDataType {
        File,
        Reference
    }

    public static void main(String... args) {
        /*!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!*/
        args = new String[]{"--help", "greg"};
        /*!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!*/
        final ArgumentParser argumentParser = new ArgumentParser(args);
        final Map<String, String[]> hashMap = argumentParser.getArgumentedMap();
        final String message = getMapStatusMessage(hashMap);
        if (!message.equals(SUCCESS)) {
            exit(message);
        }
        final File[] files = getFiles(hashMap);
    }

    private static File[] getFiles(final Map<String, String[]> hashMap) {
        final DownloadDataType downloadType = getDownloadType(hashMap);
        if (downloadType == DownloadDataType.Reference) {
            return new File[]{new File(hashMap.get(REFERENCE)[0], hashMap.get(PATH)[0])};
        } else if (downloadType == DownloadDataType.File) {
            final String path = hashMap.get(FILE)[0];
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
            return DownloadDataType.Reference;
        }
        if (hashMap.get(FILE).length != 0) {
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
        if ((hashMap.get(REFERENCE).length == 0 && hashMap.get(FILE).length == 0)
                || (!hashMap.containsKey(REFERENCE) && !hashMap.containsKey(FILE))) {
            return generateSyntaxErrorMessage();
        }
        if (hashMap.get(REFERENCE).length != 0 && hashMap.get(FILE).length != 0) {
            return generateLackOfDataMessage();
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
