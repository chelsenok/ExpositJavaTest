package downloadmanager.reader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import downloadmanager.file.File;

public class CsvReader implements Reader {

    private final String SEPARATOR = ",";

    public File[] readFiles(final String path) {
        BufferedReader br = null;
        try {
            final List<File> files = new ArrayList<>();
            br = new BufferedReader(new FileReader(path));
            String line;
            while ((line = br.readLine()) != null) {
                final String[] content = line.split(SEPARATOR);
                files.add(new File(
                        content[0],
                        content[1]
                ));
            }
            return files.toArray(new File[files.size()]);
        } catch (final Exception ignored) {
            return null;
        } finally {
            try {
                br.close();
            } catch (final Exception ignored) {
            }
        }
    }
}
