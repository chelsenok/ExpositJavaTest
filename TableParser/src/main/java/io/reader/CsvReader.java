package io.reader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvReader implements Reader {

    private final String SEPARATOR = ";";
    private BufferedReader mReader;
    private String mNextLine;

    public CsvReader(final String path) {
        try {
            mReader = new BufferedReader(new FileReader(path));
            mNextLine = mReader.readLine();
        } catch (final IOException ignored) {
        }
    }

    public String[] readLineContent() {
        final String[] strings = mNextLine.split(SEPARATOR);
        try {
            mNextLine = mReader.readLine();
        } catch (final Exception e) {
            mNextLine = null;
        }
        return strings;
    }

    @Override
    public boolean hasNext() {
        return mNextLine != null;
    }
}
