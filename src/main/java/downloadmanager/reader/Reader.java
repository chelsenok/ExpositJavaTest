package downloadmanager.reader;

import downloadmanager.File;

public interface Reader {

    /**
     * @param path path to the file in your filesystem
     * @return
     *      File[] - file exists and properly formed<br>
     *      null - file corrupted
     */
    File[] readFiles(final String path, final String downloadPath);
}
