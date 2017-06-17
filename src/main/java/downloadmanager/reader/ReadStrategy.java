package downloadmanager.reader;

import downloadmanager.File;

public interface ReadStrategy {

    /**
     * @param path path to the file in your filesystem
     * @return
     *      File[] - file exists and properly formed<br>
     *      null - file corrupted
     */
    File[] readFiles(final String path);
}
