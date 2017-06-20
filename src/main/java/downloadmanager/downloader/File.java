package downloadmanager.downloader;

import org.apache.commons.validator.routines.UrlValidator;

import java.net.MalformedURLException;
import java.net.URL;

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
        try {
            return new UrlValidator().isValid(reference) && Downloader.getFileSize(new URL(reference)) != -1;
        } catch (MalformedURLException e) {
            return false;
        }
    }
}
