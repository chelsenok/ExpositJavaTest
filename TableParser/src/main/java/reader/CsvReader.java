package reader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvReader implements Reader {

    private final String SEPARATOR = ";";

    public String[][] readContent(final String path) {
        BufferedReader br = null;
        try {
            final List<String[]> files = new ArrayList<>();
            br = new BufferedReader(new FileReader(path));
            String line;
            while ((line = br.readLine()) != null) {
                final String[] content = line.split(SEPARATOR);
                files.add(new String[]{
                        content[0],
                        content[1]
                });
            }
            return files.toArray(new String[files.size()][2]);
        } catch (final IOException ignored) {
            return null;
        } finally {
            try {
                br.close();
            } catch (final IOException ignored) {
            }
        }
    }
}
