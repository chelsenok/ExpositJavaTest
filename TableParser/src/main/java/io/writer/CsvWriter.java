package io.writer;

import java.io.IOException;
import java.io.PrintWriter;

public class CsvWriter implements Writer {

    private final String SEPARATOR = ";";

    @Override
    public void write(final String path, final String[][] content) {
        try{
            final PrintWriter writer = new PrintWriter(path, "UTF-8");
            for (final String[] line :
                    content) {
                final StringBuilder builder = new StringBuilder();
                for (final String s :
                        line) {
                    builder.append(s).append(SEPARATOR);
                }
                builder.deleteCharAt(builder.length() - 1);
                writer.println(builder);
            }
            writer.close();
        } catch (final IOException ignored) {
        }
    }
}
