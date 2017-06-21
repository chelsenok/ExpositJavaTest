package io.writer;

import java.io.IOException;
import java.io.PrintWriter;

public class TxtWriter implements Writer {

    @Override
    public void write(final String path, final String[][] content) {
        try{
            final PrintWriter writer = new PrintWriter(path, "UTF-8");
            for (final String[] line :
                    content) {
                for (final String s :
                        line) {
                    writer.println(s);
                }
            }
            writer.close();
        } catch (final IOException ignored) {
        }
    }
}
