package parser;

import java.util.Map;

import static parser.OperationType.HELP;
import static parser.OperationType.SEARCH_STRING;
import static parser.OperationType.SOURCE_FILE;
import static parser.OperationType.WRITE_FILE;

public class ArgumentManager {

    public final String sourceFile;
    public final String writeFile;
    public final String searchString;
    public final boolean needHelp;
    public final boolean isValid;
    private final ArgumentParser mParser;

    public ArgumentManager(final String[] args) {
        mParser = new ArgumentParser(args);
        final Map<String, String[]> map = mParser.getArgumentedMap();
        sourceFile = get(map, SOURCE_FILE.getType());
        writeFile = get(map, WRITE_FILE.getType());
        searchString = get(map, SEARCH_STRING.getType());
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
}
