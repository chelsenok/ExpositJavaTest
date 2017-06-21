import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import filemanager.AccessRight;
import filemanager.FileAccessor;
import io.reader.Reader;
import io.reader.ReaderFactory;
import io.writer.Writer;
import io.writer.WriterFactory;
import parser.ArgumentManager;

public final class Main {

    private static final String APP_NAME = "tableparser";
    private static final String DONE = "Done.";
    private static final String INPUT_FILE_CORRUPTED = "Input file corrupted.";

    public static void main(final String... args) {
        final ArgumentManager argumentManager = new ArgumentManager(args);
        final ArgumentsStatusMessage message = getArgumentsStatusMessage(argumentManager);
        if (message != (ArgumentsStatusMessage.SUCCESS)) {
            exit(message.getMessage(APP_NAME));
        }
        final Reader reader = ReaderFactory.getStrategy(
                argumentManager.sourceFile,
                FileAccessor.getFileExtension(argumentManager.sourceFile)
        );
        final Writer writer = WriterFactory.getStrategy(FileAccessor.getFileExtension(argumentManager.writeFile));
        if (reader == null || writer == null) {
            exit(ArgumentsStatusMessage.UNKNOWN_FILE_FORMAT.getMessage(APP_NAME));
        }
        final List<String[]> list = new ArrayList<>();
        final Set<Integer> set = getContainSearchStringColumnIndexes(reader, argumentManager.searchString, list);
        final String[][] response = getIndexedColumns(
                list.toArray(new String[list.size()][]),
                set.stream().mapToInt(i -> i).toArray()
        );
        if (response == null) {
            exit(INPUT_FILE_CORRUPTED);
        }
        writer.write(argumentManager.writeFile, response);
        System.out.println(DONE);
    }

    private static String[][] getIndexedColumns(final String[][] table, final int[] indexes) {
        final String[][] response = new String[indexes.length][table.length];
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < indexes.length; j++) {
                try {
                    response[j][i] = table[i][indexes[j]];
                } catch (final Exception e) {
                    return null;
                }
            }
        }
        return response;
    }

    private static Set<Integer> getContainSearchStringColumnIndexes(final Reader reader,
                                                                    final String searchString,
                                                                    final Collection<String[]> list) {
        final Set<Integer> set = new HashSet<>();
        while (reader.hasNext()) {
            final String[] strings = reader.readLineContent();
            for (int i = 0; i < strings.length; i++) {
                if (strings[i].equals(searchString) || strings[i].matches(searchString)) {
                    set.add(i);
                }
            }
            list.add(strings);
        }
        return set;
    }

    private static ArgumentsStatusMessage getArgumentsStatusMessage(final ArgumentManager manager) {
        if (manager.isValid && manager.needHelp) {
            return ArgumentsStatusMessage.HELP;
        }
        if (!manager.isValid || manager.searchString == null || manager.writeFile == null || manager.sourceFile == null) {
            return ArgumentsStatusMessage.SYNTAX_ERROR;
        }
        if (FileAccessor.getAccessRight(manager.writeFile) == AccessRight.DENIED) {
            return ArgumentsStatusMessage.ACCESS_DENIED;
        }
        if (!FileAccessor.fileExist(manager.sourceFile)) {
            return ArgumentsStatusMessage.NON_EXISTENT_FILE;
        }
        if (manager.sourceFile.equals(manager.writeFile)) {
            return ArgumentsStatusMessage.SAME_INPUT_OUTPUT;
        }
        return ArgumentsStatusMessage.SUCCESS;
    }

    private static void exit(final String message) {
        System.out.println(message);
        System.exit(1);
    }
}
