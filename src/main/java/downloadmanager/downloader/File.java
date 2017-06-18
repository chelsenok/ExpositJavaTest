package downloadmanager.downloader;

import org.apache.commons.validator.routines.UrlValidator;

public class File {

    public final String link;
    public final String path;
    public final boolean isWritable;

    public File(final String reference, final String path) {
        link = reference;
        if (!isLinkValid(link)) {
            this.path = path;
            isWritable = false;
        } else {
            this.path = getFullPath(path, link);
            isWritable = isWritable(getFolderPath(this.path));
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
        return new UrlValidator().isValid(reference) && isLinkDownloadFile(reference);
    }

    private boolean isLinkDownloadFile(final String reference) {
        return reference.substring(reference.lastIndexOf('/')).contains(".");
    }
}
