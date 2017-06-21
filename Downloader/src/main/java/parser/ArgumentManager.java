package parser;

import java.util.Map;

import static parser.OperationType.DOWNLOAD_PATH;
import static parser.OperationType.FILE_PATH;
import static parser.OperationType.HELP;
import static parser.OperationType.REFERENCE;
import static parser.OperationType.THREADS;

public class ArgumentManager {

    public final String downloadPath;
    public final String filePath;
    public final String reference;
    public final int threads;
    public final boolean needHelp;
    public final boolean isValid;
    private final ArgumentParser mParser;

    public ArgumentManager(final String[] args) {
        mParser = new ArgumentParser(args);
        final Map<String, String[]> map = mParser.getArgumentedMap();
        downloadPath = get(map, DOWNLOAD_PATH.getType());
        filePath = get(map, FILE_PATH.getType());
        reference = get(map, REFERENCE.getType());
        threads = getThreads(map, THREADS.getType());
        needHelp = needHelp(map, HELP.getType());
        isValid = isValid(map, needHelp);
    }

    private String get(final Map<String, String[]> map, final String type) {
        if (map.containsKey(type) && map.get(type).length == 1) {
            return map.get(type)[0];
        } else {
            return null;
        }
    }

    private boolean needHelp(final Map<String, String[]> map, final String type) {
        return map.containsKey(type) && map.get(type).length == 0;
    }

    private boolean isValid(final Map<String, String[]> map, final boolean needHelp) {
        if (map.isEmpty() || (needHelp && map.size() > 1) || mParser.isOperationRepeated()) {
            return false;
        }
        for (final Map.Entry<String, String[]> entry :
                map.entrySet()) {
            boolean exist = false;
            for (final OperationType type :
                    OperationType.values()) {
                if (entry.getKey().equals(type.getType())) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                return false;
            }
        }
        return true;
    }

    private int getThreads(final Map<String, String[]> map, final String type) {
        try {
            return Integer.valueOf(get(map, THREADS.getType()));
        } catch (NumberFormatException ignored) {
            return 1;
        }
    }
}
