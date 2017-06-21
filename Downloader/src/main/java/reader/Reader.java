package reader;

public interface Reader {

    /**
     * @param path path to the file in your filesystem
     * @return
     *      String[][] - file exists and properly formed<br>
     *      null - file corrupted
     */
    String[][] readContent(final String path);
}
