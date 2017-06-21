package downloader;

import downloader.filemanager.AccessRight;
import downloader.filemanager.FileAccessor;

public class File {

    final String link;
    final String path;
    final boolean isWritable;

    public File(final String reference, final String path) {
        link = reference;
        if (!isLinkValid(link)) {
            this.path = path;
            isWritable = false;
        } else {
            this.path = getFullPath(path, link);
            isWritable = FileAccessor.getAccessRight(this.path) != AccessRight.DENIED;
        }
    }

    private String getFullPath(final String path, final String reference) {
        String p = path;
        if (p == null || "".equals(p.trim())) {
            p = System.getProperty("user.home") + "/Downloads/" + Downloader.getFileName(reference);
        } else if (new java.io.File(path).isDirectory()) {
            p += '/' + Downloader.getFileName(reference);
        }
        return p;
    }

    private boolean isLinkValid(final String reference) {
        return Downloader.getFileSize(reference) != -1;
    }
}
