package io.reader;

public abstract class ReaderFactory {

    private static final String CSV = "csv";

    public static Reader getStrategy(final String path, final String fileFormat) {
        switch (fileFormat) {
            case CSV:
                return new CsvReader(path);
        }
        return null;
    }

}
