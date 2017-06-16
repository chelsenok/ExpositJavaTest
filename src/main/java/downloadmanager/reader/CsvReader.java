package downloadmanager.reader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import downloadmanager.File;

public class CsvReader implements ReadStrategy {

    private final String SEPARATOR = ",";

    public File[] readFiles(final String path) {
        final List<File> files = new ArrayList<>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(path));
            String line;
            while ((line = br.readLine()) != null) {
                final String[] content = line.split(SEPARATOR);
                files.add(new File(
                        content[0],
                        content[1]
                ));
            }
        } catch (final Exception ignored) {
        } finally {
            try {
                br.close();
            } catch (final Exception ignored) {
            }
        }
        return files.toArray(new File[files.size()]);
    }
}
