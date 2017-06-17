package downloadmanager.parser;

import java.util.Map;

import static downloadmanager.parser.OperationTypes.DownloadPath;
import static downloadmanager.parser.OperationTypes.FilePath;
import static downloadmanager.parser.OperationTypes.Help;
import static downloadmanager.parser.OperationTypes.Reference;
import static downloadmanager.parser.OperationTypes.Threads;

public class ArgumentManager {

    public final String downloadPath;
    public final String filePath;
    public final String reference;
    public final int threads;
    public final boolean needHelp;
    public final boolean isValid;

    public ArgumentManager(final String[] args) {
        final Map<String, String[]> map = new ArgumentParser(args).getArgumentedMap();
        downloadPath = (String) get(map, DownloadPath.getType());
        filePath = (String) get(map, FilePath.getType());
        reference = (String) get(map, Reference.getType());
        threads = getThreads(map, Threads.getType());
        needHelp = needHelp(map, Help.getType());
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
            for (final OperationTypes type :
                    OperationTypes.values()) {
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
        final Integer integer = (Integer) get(map, Threads.getType());
        if (integer == null) {
            return  1;
        } else {
            return integer;
        }
    }
}
