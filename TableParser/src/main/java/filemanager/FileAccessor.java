package filemanager;

import com.sun.istack.internal.NotNull;

import java.io.File;
import java.io.IOException;

public final class FileAccessor {

    public static AccessRight getAccessRight(@NotNull String path) {
        if (new File(path).isDirectory()) {
            path += "/gkjhreughuierhgkjdfngkjhreguhergiuh.zxc";
        }
        try {
            final File file = new File(path);
            final boolean response = file.createNewFile();
            if (!response) {
                if (file.isFile()) {
                    return AccessRight.FILE_EXIST;
                } else {
                    return AccessRight.DENIED;
                }
            }
            file.delete();
            return AccessRight.GRANTED;
        } catch (final IOException e) {
            return AccessRight.DENIED;
        }
    }

    public static String addFileIndex(final String path, final int index) {
        final String directory;
        String file = path;
        final int folderIndex = path.lastIndexOf('/');
        if (folderIndex != -1) {
            directory = path.substring(0, folderIndex + 1);
            file = path.substring(folderIndex + 1);
        } else {
            directory = "";
        }
        return directory +
                file.substring(0, file.indexOf('.')) +
                " (" + index + ")" +
                file.substring(file.indexOf('.'));
    }
}