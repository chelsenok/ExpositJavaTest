package downloadmanager.file;

public class File {

    private final String mReference;
    private final String mPath;
    public final boolean isWritable;

    public File(final String reference, final String path) {
        mReference = reference;
        mPath = getPath(path, reference);
        isWritable = isWritable(mPath);
    }

    private boolean isWritable(final String path) {
        return new java.io.File(path).canWrite();
    }

    private String getPath(final String path, final String reference) {
        String p = path;
        if (p == null) {
            p = System.getProperty("user.home") + "/Downloads" + getFileNameFromLink(reference);
        } else if (new java.io.File(path).isDirectory()) {
            p += getFileNameFromLink(reference);
        }
        return p;
    }

    private String getFileNameFromLink(final String link) {
        return link.substring(link.lastIndexOf('/'));
    }
}
