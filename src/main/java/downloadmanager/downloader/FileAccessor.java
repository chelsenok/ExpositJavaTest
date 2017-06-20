package downloadmanager.downloader;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.io.File;

public final class FileAccessor {

    public static boolean isWritable(@NotNull String path) {
        if (path.lastIndexOf('/') > path.lastIndexOf('.')) {
            path += "/gkjhreughuierhgkjdfngkjhreguhergiuh.zxc";
        }
        try {
            File file = new File(path);
            boolean response = file.createNewFile();
            if (!response) {
                return response;
            }
            file.delete();
            return response;
        } catch (IOException e) {
            return false;
        }
    }

}
