package downloadmanager.parser;

import java.util.Map;

import static downloadmanager.parser.OperationType.DOWNLOAD_PATH;
import static downloadmanager.parser.OperationType.FILE_PATH;
import static downloadmanager.parser.OperationType.HELP;
import static downloadmanager.parser.OperationType.REFERENCE;
import static downloadmanager.parser.OperationType.THREADS;

public class ArgumentManager {

    public final String downloadPath;
    public final String filePath;
    public final String reference;
    public final int threads;
    public final boolean needHelp;
    public final boolean isValid;

    public ArgumentManager(final String[] args) {
        final Map<String, String[]> map = new ArgumentParser(args).getArgumentedMap();
        downloadPath = (String) get(map, DOWNLOAD_PATH.getType());
        filePath = (String) get(map, FILE_PATH.getType());
        reference = (String) get(map, REFERENCE.getType());
        threads = getThreads(map, THREADS.getType());
        needHelp = needHelp(map, HELP.getType());
        isValid = isValid(map, needHelp);
    }

    private Object get(final Map<String, String[]> map, final String type) {
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
        if (map.isEmpty() || (needHelp && map.size() > 1)) {
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
        final Integer integer = (Integer) get(map, THREADS.getType());
        if (integer == null) {
            return  1;
        } else {
            return integer;
        }
    }
}