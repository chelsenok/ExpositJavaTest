package downloadmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArgumentParser {

    private final String[] mArgs;
    private final char OPERATION_PREFIX = '-';

    public ArgumentParser(final String[] args) {
        mArgs = args;
    }

    public Map<String, String[]> getArgumentedMap() {
        if (mArgs == null || mArgs.length == 0 || !isOperation(mArgs[0])) {
            return new HashMap<String, String[]>(0);
        }
        String currentOperation = mArgs[0];
        List<String> list = new ArrayList<String>();
        final Map<String, String[]> hashMap = new HashMap<String, String[]>();
        for (int i = 1; i < mArgs.length; i++) {
            if (isOperation(mArgs[i])) {
                final String[] array = new String[list.size()];
                hashMap.put(currentOperation, list.toArray(array));
                list = new ArrayList<String>();
                currentOperation = mArgs[i];
            } else {
                list.add(mArgs[i]);
            }
        }
        put(hashMap, currentOperation, list);
        return hashMap;
    }

    private void put(final Map<String, String[]> map, final String operation, final List<String> list) {
        final String[] array = new String[list.size()];
        map.put(operation, list.toArray(array));
    }

    private boolean isOperation(final CharSequence arg) {
        return arg.charAt(0) == OPERATION_PREFIX;
    }
}
