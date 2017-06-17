package downloadmanager.file;

public class File {

    private final String mReference;
    private final String mPath;

    public File(final String reference, final String path) {
        final java.io.File file = new java.io.File(path);
        mReference = reference;
        mPath = path;
    }
}
