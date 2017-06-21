package io.writer;

public abstract class WriterFactory {

    private static final String CSV = "csv";
    private static final String TXT = "txt";

    public static Writer getStrategy(final String fileFormat) {
        switch (fileFormat) {
            case CSV:
                return new CsvWriter();
            case TXT:
                return new TxtWriter();
        }
        return null;
    }

}
