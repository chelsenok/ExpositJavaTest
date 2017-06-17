package downloadmanager;

import org.apache.commons.validator.routines.UrlValidator;

public class File {

    private final String mReference;
    private final String mPath;
    public final boolean isWritable;

    public File(final String reference, final String path) {
        mReference = reference;
        if (!isLinkValid(mReference)) {
            mPath = path;
            isWritable = false;
        } else {
            mPath = getFullPath(path, mReference);
            isWritable = isWritable(getFolderPath(mPath));
        }
    }

    private String getFolderPath(final String path) {
        return path.substring(0, path.lastIndexOf('/'));
    }

    private boolean isWritable(final String path) {
        return new java.io.File(path).canWrite();
    }

    private String getFullPath(final String path, final String reference) {
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

    private boolean isLinkValid(final String reference) {
        return new UrlValidator().isValid(reference);
    }
}
