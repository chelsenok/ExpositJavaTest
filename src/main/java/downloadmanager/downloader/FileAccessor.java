package downloadmanager.downloader;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public final class FileAccessor {

    public static AccessRight getAccessRight(@NotNull String path) {
        if (new File(path).isDirectory()) {
            path += "/gkjhreughuierhgkjdfngkjhreguhergiuh.zxc";
        }
        try {
            File file = new File(path);
            boolean response = file.createNewFile();
            if (!response) {
                if (file.isFile()) {
                    return AccessRight.FILE_EXIST;
                } else {
                    return AccessRight.DENIED;
                }
            }
            file.delete();
            return AccessRight.GRANTED;
        } catch (IOException e) {
            return AccessRight.DENIED;
        }
    }

    public static String addFileIndex(String path, int index) {
        String directory;
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
